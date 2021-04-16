package me.resp.cli.command;

import java.io.PrintWriter;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.shell.jline3.PicocliCommands;

/** Top-level command that just prints help. */
@Component
@Command(
    name = "",
    description = {
      "Example interactive shell with completion and autosuggestions. "
          + "Hit @|magenta <TAB>|@ to see available commands.",
      "Hit @|magenta ALT-S|@ to toggle tailtips.",
      ""
    },
    footer = {"", "Press Ctrl-D to exit."},
    subcommands = {
      MyCommand.class,
      PicocliCommands.ClearScreen.class,
      CommandLine.HelpCommand.class
    })
public class CliCommands implements Runnable {
  PrintWriter out = new PrintWriter(System.out);;

  public void run() {
    out.println(new CommandLine(this).getUsageMessage());
  }
}
