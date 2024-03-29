/* Copyright 2009-2012 David Hadka
 * 
 * This file is part of the MOEA Framework.
 * 
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * The MOEA Framework is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */

package moeaexamples;
import java.io.File;
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

/**
 * Similar to Example4, sockets can be used instead of standard I/O for
 * communicating with the external process.  Run the command 'make' in the
 * ./auxiliary/c/ folder to compile the executable.  This example will only
 * work on POSIX (Unix-like) systems.
 */
public class Example6 {

	/**
	 * Notice that the only change is in the constructor, where the hostname and
	 * port are specified.
	 */
	public static class MyDTLZ2 extends ExternalProblem {

		public MyDTLZ2() throws IOException {
			super("localhost", ExternalProblem.DEFAULT_PORT);
		}

		@Override
		public String getName() {
			return "DTLZ2";
		}

		@Override
		public int getNumberOfVariables() {
			return 11;
		}

		@Override
		public int getNumberOfObjectives() {
			return 2;
		}

		@Override
		public int getNumberOfConstraints() {
			return 0;
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(0.0, 1.0));
			}

			return solution;
		}
		
	}
	
	public static void main(String[] args) throws IOException, 
	InterruptedException {
		//check if the executable exists
		File file = new File("./auxiliary/c/dtlz2_socket.exe");
		
		if (!file.exists()) {
			System.err.println("Please compile the executable by running make in the ./auxiliary/c/ folder");
			return;
		}
		
		//run the executable and wait one second for the process to startup
		new ProcessBuilder(file.toString()).start();
		Thread.sleep(1000);
		
		//configure and run the DTLZ2 function
		NondominatedPopulation result = new Executor()
				.withProblemClass(MyDTLZ2.class)
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.run();
				
		//display the results
		for (Solution solution : result) {
			System.out.print(solution.getObjective(0));
			System.out.print(' ');
			System.out.println(solution.getObjective(1));
		}
	}
	
}
