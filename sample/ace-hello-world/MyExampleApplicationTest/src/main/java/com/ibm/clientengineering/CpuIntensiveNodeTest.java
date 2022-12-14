package com.ibm.clientengineering;

import org.junit.jupiter.api.Test;

import com.ibm.integration.test.v1.NodeSpy;
import com.ibm.integration.test.v1.SpyObjectReference;
import com.ibm.integration.test.v1.TestMessageAssembly;
import com.ibm.integration.test.v1.exception.TestException;

import static com.google.common.truth.Truth.assertThat;

public class CpuIntensiveNodeTest {

	@Test
	public void test() throws TestException {

		// Define the SpyObjectReference
		SpyObjectReference nodeReference = new SpyObjectReference().application("MyExampleApplication")
				.messageFlow("CpuIntensiveFlow").node("CalculatePrimeNumbers");

		// Initialise a NodeSpy
		NodeSpy nodeSpy = new NodeSpy(nodeReference);
		
		// Declare a new TestMessageAssembly object for the message being sent into the node
		TestMessageAssembly inputMessageAssembly = new TestMessageAssembly();
						
		// Call the message flow node with the Message Assembly
		nodeSpy.evaluate(inputMessageAssembly, true, "in");
			
		TestMessageAssembly outputAssembly = nodeSpy.propagatedMessageAssembly("out", 1);
		
		String output = outputAssembly.getMessageBodyAsString();
				
		System.out.println("output: " + output);
		assertThat(output).contains("GIT_COMMIT");
	}
}
