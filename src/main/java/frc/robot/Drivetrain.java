package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Instrum;


public class Drivetrain {
    
    private final WPI_TalonFX m_leftMotorOne;
    private final WPI_TalonFX m_leftMotorTwo;
    private final WPI_TalonFX m_leftMotorThree;

    private final WPI_TalonFX m_RightMotorFour;
    private final WPI_TalonFX m_RightMotorFive;
    private final WPI_TalonFX m_RightMotorSix;

    private final MotorControllerGroup left;
    private final MotorControllerGroup right;

    private final DifferentialDrive drive;
    



    StringBuilder Lsb = new StringBuilder();
    StringBuilder Rsb = new StringBuilder();



    int _smoothing = 0;
    int _pov = -1;

    double p = 0.2;
    double i = 0;
    double d = 0;
    double f = 0.0682;
    double izone = 0;
    double peak = 1;
    int timeout = 30;
    
    public Drivetrain() {
      m_leftMotorOne = new WPI_TalonFX(1);
      m_leftMotorTwo = new WPI_TalonFX(2);
      m_leftMotorThree = new WPI_TalonFX(3);
      m_RightMotorFour = new WPI_TalonFX(4);
      m_RightMotorFive = new WPI_TalonFX(5);
      m_RightMotorSix = new WPI_TalonFX(6);


      
      right = new MotorControllerGroup(m_RightMotorFour, m_RightMotorFive, m_RightMotorSix);
      left = new MotorControllerGroup(m_leftMotorThree, m_leftMotorOne, m_leftMotorTwo);
      drive = new DifferentialDrive(right, left);


      configMotionMagic();
      m_leftMotorTwo.follow(m_leftMotorOne);
      m_leftMotorThree.follow(m_leftMotorThree);
      m_RightMotorFive.follow(m_RightMotorFour);
      m_RightMotorSix.follow(m_RightMotorFour);


      m_leftMotorOne.setNeutralMode(NeutralMode.Brake);
      m_leftMotorTwo.setNeutralMode(NeutralMode.Brake);
      m_leftMotorThree.setNeutralMode(NeutralMode.Brake);
      m_RightMotorFour.setNeutralMode(NeutralMode.Brake);
      m_RightMotorFive.setNeutralMode(NeutralMode.Brake);
      m_RightMotorSix.setNeutralMode(NeutralMode.Brake);


      }



    @SuppressWarnings("ParameterName")
    public void drive(double speed, double rotation, XboxController _joy) {

      double leftYstick = -1.0 * speed; /* left-side Y for Xbox360Gamepad */
      double rghtYstick = -1.0 * _joy.getRightY(); /* right-side Y for Xbox360Gamepad */
      if (Math.abs(leftYstick) < 0.10) { leftYstick = 0; } /* deadband 10% */
      if (Math.abs(rghtYstick) < 0.10) { rghtYstick = 0; } /* deadband 10% */

      double LeftmotorOutput = m_leftMotorOne.getMotorOutputPercent();

      double RightmotorOutput = m_RightMotorFour.getMotorOutputPercent();

    Lsb.append("\tOut%:");
		Lsb.append(LeftmotorOutput);
		Lsb.append("\tVel:");
		Lsb.append(m_leftMotorOne.getSelectedSensorVelocity(0));

    Rsb.append("\tOut%:");
		Rsb.append(RightmotorOutput);
		Rsb.append("\tVel:");
		Rsb.append(m_RightMotorFour.getSelectedSensorVelocity(0));

    if(_joy.getRightBumper()){
      double targetPos = rghtYstick * 2048 * 10.0;
      m_leftMotorOne.set(TalonFXControlMode.MotionMagic, targetPos);

    /* Append more signals to print when in speed mode */
    Lsb.append("\terr:");
    Lsb.append(m_leftMotorOne.getClosedLoopError(0));
    Lsb.append("\ttrg:");
    Lsb.append(targetPos);

    m_RightMotorFour.set(TalonFXControlMode.MotionMagic, targetPos);

    /* Append more signals to print when in speed mode */
    Rsb.append("\terr:");
    Rsb.append(m_leftMotorOne.getClosedLoopError(0));
    Rsb.append("\ttrg:");
    Rsb.append(targetPos);
    }
    else{
      drive.arcadeDrive(speed, rotation);
    }

    if(_joy.getAButtonReleased()){
      m_leftMotorOne.setSelectedSensorPosition(0);
      m_RightMotorFour.setSelectedSensorPosition(0);
    }

    int pov = _joy.getPOV();
		if (_pov == pov) {
			/* no change */
		} else if (_pov == 180) { // D-Pad down
			/* Decrease smoothing */
			_smoothing--;
			if (_smoothing < 0)
				_smoothing = 0;
        m_leftMotorOne.configMotionSCurveStrength(_smoothing);
        m_RightMotorFour.configMotionSCurveStrength(_smoothing);

			System.out.println("Smoothing is set to: " + _smoothing);
		} else if (_pov == 0) { // D-Pad up
			/* Increase smoothing */
			_smoothing++;
			if (_smoothing > 8)
				_smoothing = 8;
        m_leftMotorOne.configMotionSCurveStrength(_smoothing);
        m_RightMotorFour.configMotionSCurveStrength(_smoothing);

			System.out.println("Smoothing is set to: " + _smoothing);
		}
		_pov = pov;
    Instrum.Process(m_leftMotorOne, Lsb);
    }



    public void configMotionMagic(){

    m_leftMotorOne.configFactoryDefault();
    m_RightMotorFour.configFactoryDefault();

    m_leftMotorOne.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, timeout);
    m_RightMotorFour.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, timeout);

    m_leftMotorOne.configNeutralDeadband(0.001, timeout);
    m_RightMotorFour.configNeutralDeadband(0.001, timeout);

    m_leftMotorOne.setSensorPhase(false);
    m_RightMotorFour.setSensorPhase(false);

    m_leftMotorOne.setInverted(true);
    m_RightMotorFour.setInverted(false);

    m_leftMotorOne.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, timeout);
    m_RightMotorFour.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, timeout);

    m_leftMotorOne.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, timeout);
    m_RightMotorFour.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, timeout);

    m_leftMotorOne.configNominalOutputForward(0, timeout);
    m_leftMotorOne.configNominalOutputReverse(0, timeout);
    m_leftMotorOne.configPeakOutputForward(.8, timeout);
    m_leftMotorOne.configPeakOutputReverse(-.8, timeout);

    m_RightMotorFour.configNominalOutputForward(0, timeout);
    m_RightMotorFour.configNominalOutputReverse(0, timeout);
    m_RightMotorFour.configPeakOutputForward(.8, timeout);
    m_RightMotorFour.configPeakOutputReverse(-.8, timeout);

    m_leftMotorOne.selectProfileSlot(0, 0);
    m_leftMotorOne.config_kF(0, f, timeout);
    m_leftMotorOne.config_kP(0, p, timeout);
    m_leftMotorOne.config_kI(0, i, timeout);
    m_leftMotorOne.config_kD(0, d, timeout);

    m_RightMotorFour.selectProfileSlot(0, 0);
    m_RightMotorFour.config_kF(0, f, timeout);
    m_RightMotorFour.config_kP(0, p, timeout);
    m_RightMotorFour.config_kI(0, i, timeout);
    m_RightMotorFour.config_kD(0, d, timeout);

    m_leftMotorOne.configMotionCruiseVelocity(15000, timeout);
    m_leftMotorOne.configMotionAcceleration(6000, timeout);

    m_RightMotorFour.configMotionCruiseVelocity(15000, timeout);
    m_RightMotorFour.configMotionAcceleration(6000, timeout);

    m_leftMotorOne.setSelectedSensorPosition(0, 0, timeout);
    m_RightMotorFour.setSelectedSensorPosition(0, 0, timeout);
    }

    public void disableMotionMagic(){
      m_leftMotorOne.set(ControlMode.PercentOutput, 0);
      m_RightMotorFour.set(ControlMode.PercentOutput, 0);
    }

}
