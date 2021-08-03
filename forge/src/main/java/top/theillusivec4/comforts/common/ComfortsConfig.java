/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.comforts.ComfortsMod;

public class ComfortsConfig {

  public static final ForgeConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + ComfortsMod.MOD_ID + ".config.";

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class Server {

    public final ForgeConfigSpec.BooleanValue autoUse;
    public final ForgeConfigSpec.BooleanValue wellRested;
    public final ForgeConfigSpec.DoubleValue sleepyFactor;
    public final ForgeConfigSpec.BooleanValue nightHammocks;
    public final ForgeConfigSpec.DoubleValue sleepingBagBreakage;
    public final ForgeConfigSpec.ConfigValue<List<String>> sleepingBagDebuffs;
    public final ForgeConfigSpec.BooleanValue insulatedSleepingBag;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("server");

      autoUse = builder.comment("Set to true to automatically use sleeping bags when placed")
          .translation(CONFIG_PREFIX + "autoUse").define("autoUse", true);

      wellRested = builder
          .comment("Set to true to prevent sleeping depending on how long you previously slept")
          .translation(CONFIG_PREFIX + "wellRested").define("wellRested", false);

      sleepyFactor = builder.comment(
          "If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)")
          .translation(CONFIG_PREFIX + "sleepyFactor")
          .defineInRange("sleepyFactor", 2.0D, 1.0D, 20.0D);

      nightHammocks = builder.comment("Set to true to enable sleeping in hammocks at night")
          .translation(CONFIG_PREFIX + "nightHammocks").define("nightHammocks", false);

      sleepingBagBreakage = builder.comment("The chance that a sleeping bag will break upon usage")
          .translation(CONFIG_PREFIX + "sleepingBagBreakage")
          .defineInRange("sleepingBagBreakage", 0.0D, 0.0D, 1.0D);

      sleepingBagDebuffs = builder.comment(
          "List of debuffs to apply to players after using the sleeping bag\n"
              + "Format: [effect] [duration(secs)] [power]")
          .translation(CONFIG_PREFIX + "sleepingBagDebuffs").worldRestart()
          .define("sleepingBagDebuffs", new ArrayList<>());

      insulatedSleepingBag = builder.comment(
          "[Survive Mod Integration] Set to true to have sleeping bags increase your body temperature after sleeping")
          .translation(CONFIG_PREFIX + "insulatedSleepingBag")
          .define("insulatedSleepingBag", false);
    }
  }

  public static boolean autoUse;
  public static boolean wellRested;
  public static double sleepyFactor;
  public static boolean nightHammocks;
  public static double sleepingBagBreakage;
  public static List<MobEffectInstance> sleepingBagDebuffs;
  public static boolean insulatedSleepingBag;

  public static void bake() {
    autoUse = SERVER.autoUse.get();
    wellRested = SERVER.wellRested.get();
    sleepyFactor = SERVER.sleepyFactor.get();
    nightHammocks = SERVER.nightHammocks.get();
    sleepingBagBreakage = SERVER.sleepingBagBreakage.get();
    sleepingBagDebuffs = new ArrayList<>();
    insulatedSleepingBag = SERVER.insulatedSleepingBag.get();

    SERVER.sleepingBagDebuffs.get().forEach(debuff -> {
      String[] elements = debuff.split("\\s+");
      MobEffect potion = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(elements[0]));

      if (potion == null) {
        return;
      }
      int duration = 0;
      int amp = 0;
      try {
        duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
        amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
      } catch (Exception e) {
        ComfortsMod.LOGGER.error("Problem parsing sleeping bag debuffs in config!", e);
      }
      sleepingBagDebuffs.add(new MobEffectInstance(potion, duration * 20, amp - 1));
    });
  }
}
