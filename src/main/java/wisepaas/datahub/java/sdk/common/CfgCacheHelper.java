package wisepaas.datahub.java.sdk.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.Gson;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const.CfgCache;
import wisepaas.datahub.java.sdk.model.message.ConfigCacheMessage;

public class CfgCacheHelper {
    private String _cfgCacheFilePath;

    public CfgCacheHelper(String androidPackageName) {
        if (Helpers.isAndroid() == true) {
            _cfgCacheFilePath = "/data/data/" + androidPackageName + "/" + CfgCache.CfgCacheFileName;

        } else {
            _cfgCacheFilePath = new File("").getAbsolutePath() + File.separatorChar + CfgCache.CfgCacheFileName;
        }
    }

    public void addCfgToMemory(String cfg, EdgeAgent agent) {
        Gson gson = new Gson();
        agent.cfgCache = gson.fromJson(cfg, ConfigCacheMessage.class);
    }

    public void addCfgToFile(String cfg) {
        File fnew = new File(_cfgCacheFilePath);

        try {
            FileWriter fw = new FileWriter(fnew, false);
            fw.write(cfg);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ConfigCacheMessage getCfgFromFile() {
        ConfigCacheMessage result = null;
        try {
            if (new File(_cfgCacheFilePath).exists() == false) {
                return result;
            }

            String data = "";
            File myObj = new File(_cfgCacheFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            myReader.close();

            if (!data.equals("")) {
                Gson gson = new Gson();
                result = gson.fromJson(data, ConfigCacheMessage.class);
                return result;
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}