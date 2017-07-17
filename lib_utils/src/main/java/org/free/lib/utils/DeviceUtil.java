package org.free.lib.utils;

import android.os.Build;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <b>Created by Disco for AppCenter.</b>
 * <br><b>Version:</b>
 * <br><b>Profile:</b>
 * <br><b>Date:</b> 2016/12/20.
 * <br><b>Email:</b>dike_doit@163.com.
 */

public class DeviceUtil
{
    public static class CPU
    {
        /**
         * cpu名称，arm、x86
         */
         String name;
        /**
         * cpu版本，v7、v5
         */
         int version;
        /**
         * cpu指令集，neon
         */
         String instruction;

        /**
         * cpu名称，arm、x86
         * @return
         */
        public String getName()
        {
            return name;
        }

        /**
         * cpu版本，v7、v5
         * @return
         */
        public int getVersion()
        {
            return version;
        }

        /**
         * cpu指令集，neon
         * @return
         */
        public String getInstruction()
        {
            return instruction;
        }


        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("cpu_name="+name);
            builder.append("cpu_version="+version);
            builder.append("cpu_instruction="+instruction);
            return builder.toString();
        }
    }



    /**
     * [获取cpu类型和架构]
     *
     * @return 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
     */
    public static CPU getCpuArchitecture()
    {
        CPU cpu = new CPU();
        try
        {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try
            {
                String nameProcessor = "Processor";
                String nameFeatures = "Features";
                String nameModel = "model name";
                String nameCpuFamily = "cpu family";
                while (true)
                {
                    String line = br.readLine();
                    String[] pair = null;
                    if (line == null)
                    {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                    {
                        continue;
                    }
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo(nameProcessor) == 0)
                    {
                        String n = "";
                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++)
                        {
                            String temp = val.charAt(i) + "";
                            if (temp.matches("\\d"))
                            {
                                n += temp;
                            }
                            else
                            {
                                break;
                            }
                        }
                        cpu.name = "ARM";
                        cpu.version = Integer.parseInt(n);
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameFeatures) == 0)
                    {
                        if (val.contains("neon"))
                        {
                            cpu.instruction = "neon";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameModel) == 0)
                    {
                        if (val.contains("Intel"))
                        {
                            cpu.name = "INTEL";
                            cpu.instruction = "atom";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameCpuFamily) == 0)
                    {
                        cpu.version = Integer.parseInt(val);
                        continue;
                    }
                }
            }
            finally
            {
                br.close();
                ir.close();
                is.close();
            }
        }
        catch (Exception e)
        {
            //default value
            cpu.name = "arm";
            cpu.version = 7;
            cpu.instruction = "neon";
            Tracer.printStackTrace(e);
        }

        return cpu;
    }

    public static String getCpuAbi()
    {
        String abi;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            abi = Build.CPU_ABI;
        }
        else
        {
            abi = Build.SUPPORTED_ABIS[0];
        }
        return abi;
    }
}
