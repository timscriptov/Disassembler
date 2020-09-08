package com.nbsp.materialfilepicker.filter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

public class HiddenFilter implements FileFilter, Serializable {

    @Override
    public boolean accept(@NotNull File f) {
        return !f.isHidden();
    }
}