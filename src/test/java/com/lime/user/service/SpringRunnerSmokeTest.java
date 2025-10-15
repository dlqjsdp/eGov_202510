package com.lime.user.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:egovframework/spring/context-datasource.xml",
    "classpath:egovframework/spring/context-mapper.xml",
    "classpath:egovframework/spring/context-transaction.xml",
    "classpath:egovframework/spring/context-common.xml"
})
public class SpringRunnerSmokeTest {
    @Test
    public void sanity_with_spring_runner() {
        System.out.println("SPRING RUNNER SANITY");
        assertTrue(true);
    }
}
