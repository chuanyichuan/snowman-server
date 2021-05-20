package cc.kevinlu.snow.server.config.mybatis.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * @author chuan
 */
public class MyTypeGenerator {

    public static void main(String[] args) {
        List<String> warnings = new ArrayList<>();
        try {
            String configFilePath = System.getProperty("user.dir")
                    .concat("/snowman-server/src/main/resources/generatorConfig.xml");
            System.out.println("加载配置文件===" + configFilePath);
            boolean overwrite = true;
            File configFile = new File(configFilePath);
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            //ProgressCallback progressCallback = new VerboseProgressCallback();
            myBatisGenerator.generate(null);
            //myBatisGenerator.generate(progressCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

}
