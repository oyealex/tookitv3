package com.smartkit.toolbox.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.tool.ToolInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 输入文件生成器
 * 生成 input.json 文件到工作目录
 */
@Component
public class InputFileGenerator {

    private static final Logger log = LoggerFactory.getLogger(InputFileGenerator.class);

    /**
     * 输入文件名
     */
    public static final String INPUT_FILE_NAME = "input.json";

    private final ObjectMapper objectMapper;

    public InputFileGenerator() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 生成输入 JSON 文件
     *
     * @param workDir 工作目录
     * @param input   输入数据
     * @return 输入文件路径
     * @throws IOException 如果文件写入失败
     */
    public Path generateInputFile(Path workDir, ToolInput input) throws IOException {
        Path inputFile = workDir.resolve(INPUT_FILE_NAME);

        // 格式化 JSON（带缩进）
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(input);
        Files.writeString(inputFile, json);

        log.info("生成输入文件: {}", inputFile);
        return inputFile;
    }

    /**
     * 读取输入 JSON 文件
     *
     * @param inputFile 输入文件路径
     * @return 输入数据
     * @throws IOException 如果文件读取失败
     */
    public ToolInput readInputFile(Path inputFile) throws IOException {
        return objectMapper.readValue(inputFile.toFile(), ToolInput.class);
    }
}