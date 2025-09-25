package com.github.gv2011.util.swing.imp;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.swing.GuiBuilder;
import com.github.gv2011.util.swing.SwingFactory;
import com.github.gv2011.util.swing.imp.builder.GuiBuilderImp;

public final class SwingFactoryImp implements SwingFactory{

  @Override
  public GuiBuilder guiBuilder() {
    return new GuiBuilderImp();
  }

  @Override
  public Opt<Path> selectFile(final Path initialFileOrDirectory, final String title) {
    return call(()->{
      final AtomicReference<Opt<Path>> selectedPath = new AtomicReference<>(Opt.empty());
      SwingUtilities.invokeAndWait(() -> {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        final JFileChooser fileChooser = new JFileChooser();
        if(Files.isDirectory(initialFileOrDirectory)){
          fileChooser.setCurrentDirectory(initialFileOrDirectory.toFile());
        }
        else{
          fileChooser.setCurrentDirectory(initialFileOrDirectory.getParent().toFile());
          fileChooser.setSelectedFile(initialFileOrDirectory.toFile());
        }

        final int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPath.set(Opt.of(fileChooser.getSelectedFile().toPath()));
        }

        frame.dispose();
      });
      return selectedPath.get();
    });
  }

}
