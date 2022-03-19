// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Drivetrain;



public class Robot extends TimedRobot {

  public Drivetrain m_Drive = new Drivetrain();
  public XboxController m_DriveController;


  @Override
  public void robotInit() {
    m_DriveController = new XboxController(0);
  }


  @Override
  public void robotPeriodic() {
    

  }

  @Override
  public void autonomousInit() {



  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
   

    

  }

  
  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {
  m_Drive.drive(m_DriveController.getLeftY(), m_DriveController.getRightX()*.65, m_DriveController);
  }

 
  @Override
  public void disabledInit() {
    m_Drive.disableMotionMagic();
  }


  @Override
  public void disabledPeriodic() {}


  @Override
  public void testInit() {
    
  }


}
