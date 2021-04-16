package me.resp.cli.command;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
@Component
@Command(
    name = "nested",
    mixinStandardHelpOptions = true,
    subcommands = {CommandLine.HelpCommand.class},
    description = "Hosts more sub-subcommands")
public class Nested implements Runnable {
  public void run() {
    System.out.println("I'm a nested subcommand. I don't do much, but I have sub-subcommands!");
  }

  @Command(
      mixinStandardHelpOptions = true,
      subcommands = {CommandLine.HelpCommand.class},
      description = "Multiplies two numbers.")
  public void multiply(
      @Option(
              names = {"-l", "--left"},
              required = true)
          int left,
      @Option(
              names = {"-r", "--right"},
              required = true)
          int right) {
    System.out.printf("%d * %d = %d%n", left, right, left * right);
  }

  @Command(
      mixinStandardHelpOptions = true,
      subcommands = {CommandLine.HelpCommand.class},
      description = "Adds two numbers.")
  public void add(
      @Option(
              names = {"-l", "--left"},
              required = true)
          int left,
      @Option(
              names = {"-r", "--right"},
              required = true)
          int right) {
    System.out.printf("%d + %d = %d%n", left, right, left + right);
  }

  @Command(
      mixinStandardHelpOptions = true,
      subcommands = {CommandLine.HelpCommand.class},
      description = "Subtracts two numbers.")
  public void subtract(
      @Option(
              names = {"-l", "--left"},
              required = true)
          int left,
      @Option(
              names = {"-r", "--right"},
              required = true)
          int right) {
    System.out.printf("%d - %d = %d%n", left, right, left - right);
  }
}
