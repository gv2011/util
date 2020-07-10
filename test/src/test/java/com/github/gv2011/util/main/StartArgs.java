package com.github.gv2011.util.main;

import java.nio.file.Path;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.Opt;

interface StartArgs extends Bean{
	
	Opt<Path> jdk();
	
	ArtifactRef artifact();
	
	Opt<String> mainClass();
}
