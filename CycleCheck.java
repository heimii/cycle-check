package test.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhong
 * @date 2021-1-26
 */
public class CycleCheck {

    private static final String ROOT_PATH = "f:/3";

    public static void main(String[] args) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(ROOT_PATH), new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith("Service.java");
            }

            @Override
            public boolean accept(File dir, String name) {
                System.out.println("[TRACE]file-name: " + name);
                return true;
            }
        }, TrueFileFilter.INSTANCE);

        Set<String> pairSet = new HashSet<>();
        for (File file : files) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++");
            System.out.println("处理文件：" + file.getPath());
            dealSingleFile(file, pairSet);
        }
        new CycleCheckAlg().check(pairSet);
    }

    private static void dealSingleFile(File targetFile, Set<String> pairSet) throws IOException {
        Set<String> assetServiceDep = parseDependencies(targetFile);
        Map<String, Set<String>> dependencyMap = new HashMap<>();
        String name = extractFileName(targetFile);
        dependencyMap.put(name, assetServiceDep);
        Set<String> set = assetServiceDep.stream().map(item -> name + "," + item).collect(Collectors.toSet());
        pairSet.addAll(set);
    }

    private static String extractFileName(File targetFile) {
        String name = FilenameUtils.getName(targetFile.getName());
        int index = name.lastIndexOf(".");
        if (index > 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    private static Set<String> parseDependencies(File targetFile) throws IOException {
        Set<String> dependencies = new HashSet<>();
        List<String> lines = FileUtils.readLines(targetFile, "UTF-8");
        for (String line : lines) {
            String lineCode = line.trim();
            if (lineCode.startsWith("private ") && lineCode.endsWith("Service;")) {
                String serviceClassName = lineCode.split(" ")[1];
                dependencies.add(serviceClassName);
            }
        }
        return dependencies;
    }
}
