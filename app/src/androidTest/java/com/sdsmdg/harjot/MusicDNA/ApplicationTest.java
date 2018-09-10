package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="https://developer.android.com/studio/test">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
	@Test
	public void useAppContext() {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getTargetContext();

		assertEquals("com.sdsmdg.harjot.MusicDNA", appContext.getPackageName());
    }
}
