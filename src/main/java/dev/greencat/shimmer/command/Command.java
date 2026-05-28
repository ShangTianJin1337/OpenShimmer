package dev.greencat.shimmer.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public abstract class Command {
   public static MinecraftClient mc = MinecraftClient.getInstance();
   public String name;
   public String description;
   public String syntax;
   public List<String> aliases;

   public Command(String name, String description, String syntax, String... aliases) {
      this.name = name;
      this.description = description;
      this.syntax = syntax;
      this.aliases = Arrays.asList(aliases);
   }

   public void onCommand(String[] args, String command) {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSyntax() {
      return this.syntax;
   }

   public void setSyntax(String syntax) {
      this.syntax = syntax;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void setAliases(List<String> aliases) {
      this.aliases = aliases;
   }
}
