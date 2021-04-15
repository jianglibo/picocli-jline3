package me.resp.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import org.fusesource.jansi.AnsiConsole;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.resp.cli.command.CliCommands;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;

@SpringBootApplication
public class CliApplication implements CommandLineRunner, ExitCodeGenerator {
  private IFactory factory;
  private CliCommands mailCommand;
  private int exitCode;

  CliApplication(CliCommands mailCommand, IFactory factory) {
    this.mailCommand = mailCommand;
    this.factory = factory;
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  @Override
  public void run(String... args) throws Exception {
    AnsiConsole.systemInstall();
    try {
      Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
      // set up JLine built-in commands
      Builtins builtins = new Builtins(workDir, null, null);
      builtins.rename(Builtins.Command.TTOP, "top");
      builtins.alias("zle", "widget");
      builtins.alias("bindkey", "keymap");
      // set up picocli commands
      //   CliCommands commands = new CliCommands();

      //   PicocliCommandsFactory factory = new PicocliCommandsFactory();
      // Or, if you have your own factory, you can chain them like this:
      // MyCustomFactory customFactory = createCustomFactory(); // your application custom factory
      PicocliCommandsFactory ffactory = new PicocliCommandsFactory(factory); // chain the
      // factories

      CommandLine cmd = new CommandLine(mailCommand, ffactory);
      PicocliCommands picocliCommands = new PicocliCommands(cmd);

      Parser parser = new DefaultParser();
      try (Terminal terminal = TerminalBuilder.builder().build()) {
        SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, null);
        systemRegistry.setCommandRegistries(builtins, picocliCommands);
        systemRegistry.register("help", picocliCommands);

        LineReader reader =
            LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(systemRegistry.completer())
                .parser(parser)
                .variable(LineReader.LIST_MAX, 50) // max tab completion candidates
                .build();
        builtins.setLineReader(reader);
        ffactory.setTerminal(terminal);
        TailTipWidgets widgets =
            new TailTipWidgets(
                reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER);
        widgets.enable();
        KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
        keyMap.bind(new Reference("tailtip-toggle"), KeyMap.alt("s"));

        String prompt = "prompt> ";
        String rightPrompt = null;

        // start the shell and process input until the user quits with Ctrl-D
        String line;
        while (true) {
          try {
            systemRegistry.cleanUp();
            line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
            systemRegistry.execute(line);
          } catch (UserInterruptException e) {
            // Ignore
          } catch (EndOfFileException e) {
            return;
          } catch (Exception e) {
            systemRegistry.trace(e);
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      AnsiConsole.systemUninstall();
    }
    // exitCode = new CommandLine(mailCommand, factory).execute(args);
  }

  public static void main(String[] args) throws Exception {

    // System.exit(
    //     SpringApplication.exit(
    //         new SpringApplicationBuilder(CliApplication.class)
    //             .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
    //             .run(args)));

    System.exit(SpringApplication.exit(SpringApplication.run(CliApplication.class, args)));

    // ConfigurableApplicationContext ctx = SpringApplication.run(CliApplication.class, args);
    // TopCommand cmd = ctx.getBean(TopCommand.class);
    // cmd.run(args);

    // System.exit(cmd.getExitCode());

  }
}
