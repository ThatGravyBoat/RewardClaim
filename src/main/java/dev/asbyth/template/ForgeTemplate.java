package dev.asbyth.template;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "forgetemplate", name = "Forge Template", version = "1.0")
public class ForgeTemplate {
    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        // $USER = The username of the currently logged in user.
        // Simply prints out Hello, $USER.
        System.out.println("Hello, " + Minecraft.getMinecraft().getSession().getUsername() + "!");
    }
}
