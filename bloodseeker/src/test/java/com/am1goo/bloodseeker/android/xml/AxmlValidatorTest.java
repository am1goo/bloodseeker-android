package com.am1goo.bloodseeker.android.xml;

import com.am1goo.bloodseeker.android.axml.AxmlValidator;
import com.am1goo.bloodseeker.utilities.PathUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import pxb.android.axml.Axml;
import pxb.android.axml.AxmlReader;
import pxb.android.axml.Util;

public class AxmlValidatorTest {

    @Test
    public void testElements() throws IOException {
        AxmlValidator validator = new AxmlValidator("manifest")
                .root()
                .node("application")
                    .node("activity")
                        .attribute("android:name", "com.unity3d.player.UnityPlayerActivity")
                        .node("intent-filter")
                            .node("action")
                                .attribute("android:name", "android.intent.action.MAIN")
                            .parent()
                            .node("action")
                                .attribute("android:name", "android.intent.category.LAUNCHER")
                .root();

        String path = PathUtilities.join("src/test/java/com/am1goo/bloodseeker/android/xml/axml/AndroidManifest.xml");
        File file = new File(path);
        byte[] bytes = Util.readFile(file);
        AxmlReader reader = new AxmlReader(bytes);
        Axml axml = new Axml();
        reader.accept(axml);

        boolean valid = validator.validate(axml);
        Assert.assertTrue(valid);
    }
}
