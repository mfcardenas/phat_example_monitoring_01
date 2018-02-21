/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat;
import java.util.Properties;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import com.jme3.system.AppSettings;
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.*;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.environment.SpatialEnvironmentAPI;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.server.ServerAppState;
import phat.server.commands.ActivateAccelerometerServerCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 * Class example Test rum simulatios.
 * <br/>
 * Interval Execute Classificator<br/>
 * float timeToChange = 10f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 10) {<br/>
 * <br/>
 * Interval Execute Capture Data<br/>
 * float timeToChange = 2f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 2) {<br/>
 * <br/>
 * Optimal min distance between animation<br/>
 * 0.1 * 0.1 * 0.2 <br/>
 * <br/>
 * <b>Gesture</b>
 * SpinSpindle: 	abrir puerta con dificultad<br/>
 * Hands2Hips: 		llevar manos a la cadera, (dolor de espalda)<br/>
 * Hand2Belly: 		llevar la mano al vientre, (dolor de vientre)<br/>
 * Wave: 			pedir ayuda o llamar atención<br/>
 * ScratchArm: 		rascar el codo<br/>
 * LeverPole: 		molestias en el movimiento y pedir ayuda   <br/>
 * @author UCM
 */
public class ActvityMonitoringDemo implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    private BodiesAppState bodiesAppState;
    private ServerAppState serverAppState;
    private DevicesAppState devicesAppState;
    private WorldAppState worldAppState;

    private static Properties config;

    public static void main(String[] args) {
        ActvityMonitoringDemo test = new ActvityMonitoringDemo();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("PHAT");
        settings.setWidth(640);
        settings.setHeight(480);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(0.2599395f, 2.7232018f, 3.373138f));
        app.getCamera().setRotation(new Quaternion(-0.0035931943f, 0.9672268f, -0.25351822f, -0.013704466f));

        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(1 / 60f);

        worldAppState = new WorldAppState();
        worldAppState.setLandType(WorldAppState.LandType.Grass);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);

        Debug.enableDebugGrid(10, app.getAssetManager(), app.getRootNode());
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));

        bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand("Patient", 0.5f));
        bodiesAppState.runCommand(new SetStoopedBodyCommand("Patient", true));

        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));

        SetCameraToBodyCommand camCommand = new SetCameraToBodyCommand("Patient");
        camCommand.setDistance(3);
        camCommand.setFront(true);
        bodiesAppState.runCommand(camCommand);

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor1"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor1",
                SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor2"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor2",
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightHand));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor3"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor3",
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftHand));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serverAppState = new ServerAppState();
        stateManager.attach(serverAppState);

        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor1"));
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor2"));
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor3"));

        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }

            float cont = 0f;
            boolean fall = false;
            float timeToChange = 7f;
            boolean init = false;

            @Override
            public void update(float f) {
                if (!init) {
                    AccelerometerControl ac1 = devicesAppState.getDevice("sensor1")
                            .getControl(AccelerometerControl.class);
                    ac1.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart1 = new XYAccelerationsChart("Chart - Acc.", "Local accelerations Chest", "m/s2",
                            "x,y,z");
                    ac1.add(chart1);
                    chart1.showWindow();

                    AccelerometerControl ac2 = devicesAppState.getDevice("sensor2")
                            .getControl(AccelerometerControl.class);
                    ac2.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart2 = new XYAccelerationsChart("Chart - Acc.", "Local accelerations Right Hand", "m/s2",
                            "x,y,z");
                    ac2.add(chart2);
                    chart2.showWindow();

                    AccelerometerControl ac3 = devicesAppState.getDevice("sensor3")
                            .getControl(AccelerometerControl.class);
                    ac3.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart3 = new XYAccelerationsChart("Chart - Acc.", "Local accelerations Left Hand", "m/s2",
                            "x,y,z");
                    ac3.add(chart3);
                    chart3.showWindow();

                    init = true;

                }

                cont += f;
                if (cont > timeToChange && cont < timeToChange + 1f && !fall) {
                    System.out.println("Change to DrinkStanding:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                    bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "DrinkStanding"));
                    /*
                     * Gesture
                     * SpinSpindle: 	abrir puerta con dificultad
                     * Hands2Hips: 		llevar manos a la cadera, (dolor de espalda)
                     * Hand2Belly: 		llevar la mano al vientre, (dolor de vientre)
                     * Wave: 			pedir ayuda o llamar atención
                     * ScratchArm: 		rascar el codo
                     * LeverPole: 		molestias en el movimiento y pedir ayuda
                     *
                     */
                    fall = true;
                } else {
                    if (fall && cont > timeToChange + 7f) {
                        System.out.println("Change to WaveAttention:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                        bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "WaveAttention"));
                        fall = false;
                        cont = 0;
                    }
                }
            }
        });

    }
}