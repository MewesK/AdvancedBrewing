/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

public class Reference {
    public static final String MOD_ID = "AdvancedBrewing";
    public static final String MOD_NAME = "Advanced Brewing";
    public static final String VERSION_NUMBER = "@VERSION@.@BUILD_NUMBER@";
    public static final String CHANNEL_NAME = MOD_ID;
    public static final String DEPENDENCIES = "required-after:Forge@[9.10.0.800,)";
    public static final String CLIENT_PROXY_CLASS = "advancedbrewing.proxy.ProxyClient";
    public static final String SERVER_PROXY_CLASS = "advancedbrewing.proxy.Proxy";
}