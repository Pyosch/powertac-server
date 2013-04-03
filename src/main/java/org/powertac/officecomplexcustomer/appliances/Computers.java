/*
 * Copyright 2009-2012 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.officecomplexcustomer.appliances;

import java.util.Properties;
import java.util.Vector;

import org.powertac.common.repo.RandomSeedRepo;
import org.powertac.common.spring.SpringApplicationContext;
import org.powertac.officecomplexcustomer.configurations.OfficeComplexConstants;

/**
 * Lights are utilized when the persons inhabiting the house have need for them
 * to light the rooms they are in. So it's a not shifting appliance.
 * 
 * @author Antonios Chrysopoulos
 * @version 1.5, Date: 2.25.12
 */
public class Computers extends NotShiftingAppliance
{

  /**
   * This variable shows the possibility (%) that this appliance will be used
   * when workers are on break.
   */
  private double operationPercentage;

  @Override
  public void initialize (String office, Properties conf, int seed)
  {
    // Filling the base variables
    name = office + " Computers";
    saturation = Double.parseDouble(conf.getProperty("ComputersSaturation"));
    randomSeedRepo =
      (RandomSeedRepo) SpringApplicationContext.getBean("randomSeedRepo");
    gen =
      randomSeedRepo.getRandomSeed(toString(), seed, "Appliance Model" + seed);
    power =
      (int) (OfficeComplexConstants.COMPUTERS_POWER_VARIANCE
             * gen.nextGaussian() + OfficeComplexConstants.COMPUTERS_POWER_MEAN);
    cycleDuration = OfficeComplexConstants.COMPUTERS_DURATION_CYCLE;
    operationPercentage =
      Double.parseDouble(conf.getProperty("ComputersWorking"));

  }

  @Override
  Vector<Boolean> createDailyPossibilityOperationVector (int day)
  {
    Vector<Boolean> possibilityDailyOperation = new Vector<Boolean>();

    // Lights need to operate only when someone is in the house
    for (int j = 0; j < OfficeComplexConstants.QUARTERS_OF_DAY; j++) {
      if (applianceOf.isWorking(day, j) == true)
        possibilityDailyOperation.add(true);
      else
        possibilityDailyOperation.add(false);
    }

    return possibilityDailyOperation;
  }

  @Override
  public void fillDailyOperation (int weekday)
  {
    // Initializing and Creating auxiliary variables
    loadVector = new Vector<Integer>();
    dailyOperation = new Vector<Boolean>();

    // For each quarter of a day
    for (int i = 0; i < OfficeComplexConstants.QUARTERS_OF_DAY; i++) {
      loadVector.add(0);
      dailyOperation.add(false);

      if (applianceOf.isWorking(weekday, i)) {

        for (int j = 0; j < applianceOf.employeeWorkingNumber(weekday, i); j++) {
          if (gen.nextDouble() < operationPercentage) {
            dailyOperation.set(i, true);
            loadVector.set(i, loadVector.get(i) + power);
          }
        }

      }
    }

    weeklyLoadVector.add(loadVector);
    weeklyOperation.add(dailyOperation);
  }

  @Override
  public void refresh ()
  {
    fillWeeklyOperation();
    createWeeklyPossibilityOperationVector();
  }

}
