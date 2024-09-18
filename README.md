# Reactive Systems Workshop - Traffic Control & Monkey-Banana Simulation

This project is part of a **Workshop in Software Models** course, focusing on the specification and implementation of reactive systems using **Spectra**. The workshop emphasizes modeling, formal specifications, and simulations of reactive systems like traffic controllers and robot movement using the **Spectra** formal specification language and Java-based simulations.

## Project Overview

This repository contains multiple exercises where we model and simulate reactive systems using **Spectra** and Java. The project explores various topics like unrealizability, well-separation, and more.

### Exercises

1. **Traffic Control System**
   - **Goal**: Design a traffic control system to regulate two traffic lights based on environmental conditions (i.e., presence of cars) while ensuring safe and efficient traffic flow.
   - **Files**: 
     - `TrafficA1.spectra`
     - `TrafficA2.spectra`
     - `TrafficA3.spectra`
   - **Simulation**: The simulation logic is implemented in Java in the `TrafficSimulatorCmd.java` file.
   
2. **Monkey with Banana (Ex3)**
   - **Goal**: Model a scenario where a monkey interacts with a banana based on a set of conditions.
   - **Files**:
     - `MonkeyWithBanana.spectra`
     - `MonkeyWithBananaEx3.spectra`
   - **Simulation**: Implementation is supported through Java classes.

3. **Grid Movement System**
   - **Goal**: Model a system where a robot moves across a grid based on specific constraints.
   - **Files**:
     - `GridA3.spectra`
     - `GridL2.spectra`
   - **Simulation**: The robot’s movement logic is implemented and tested.

## Folder Structure

```bash
├── A1_firstController
│   ├── TrafficA1.spectra  # Basic traffic control model
├── A2_unrealizability
│   ├── TrafficA2b.spectra # Traffic model handling unrealizability
├── A3_wellseparation
│   ├── TrafficA3.spectra  # Traffic model focusing on well-separation guarantees
├── E2_execution
│   ├── src
│   │   ├── traffic
│   │       └── TrafficSimulatorCmd.java  # Main Java file for simulating the traffic model
├── L1_firstSpec
│   ├── MonkeyWithBanana.spectra  # Specification for the Monkey-Banana problem
├── L2_defsArrays
│   ├── GridL2.spectra  # Grid movement model with predicates and definitions


# Reactive Systems Simulation Project

This project implements reactive systems using Spectra specifications and Java-based simulations. The primary focus is on traffic light control systems and other reactive systems modeled using Spectra.

## Requirements

- Java 8+
- Eclipse IDE (or any other Java IDE)
- Spectra plugin installed in Eclipse for working with the specification files

## How to Run

### Spectra Specifications

1. Open the Spectra specification files (e.g., `TrafficA1.spectra`) in Eclipse.
2. Use the Spectra Plugin to check the realizability of the specification or execute the synthesis tool.

### Java Simulation

1. Navigate to the `src/traffic/TrafficSimulatorCmd.java` file.
2. Run the simulation using Eclipse or through the command line.
3. Follow the command-line prompts to input values and observe the traffic light behavior based on the environment.

## Simulation Example

When running the simulation, the system will prompt you to input certain values to define the environment, such as:

- Is car A present? (Y/n)
- Is car B present? (Y/n)
- Is there an emergency? (Y/n)

Based on these inputs, the traffic light simulation will behave accordingly, switching between different states to manage traffic flow.

---

Feel free to add any additional details or specific instructions!
