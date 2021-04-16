package me.resp.cli.command;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/** A command with some options to demonstrate completion. */
@Component
@Command(
    name = "cmd",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = {
      "Command with some options to demonstrate TAB-completion.",
      " (Note that enum values also get completed.)"
    },
    subcommands = {Nested.class, CommandLine.HelpCommand.class})
public class MyCommand implements Runnable {
  @Option(
      names = {"-v", "--verbose"},
      description = {
        "Specify multiple -v options to increase verbosity.",
        "For example, `-v -v -v` or `-vvv`"
      })
  private boolean[] verbosity = {};

  @ParentCommand CliCommands parent;

  @ArgGroup(exclusive = false)
  private MyDuration myDuration = new MyDuration();

  static class MyDuration {
    @Option(
        names = {"-d", "--duration"},
        description = "The duration quantity.",
        required = true)
    private int amount;

    @Option(
        names = {"-u", "--timeUnit"},
        description = "The duration time unit.",
        required = true)
    private TimeUnit unit;
  }

  public void run() {
    if (verbosity.length > 0) {
      parent.out.printf("Hi there. You asked for %d %s.%n", myDuration.amount, myDuration.unit);
    } else {
      parent.out.println("hi!");
    }
  }
}
