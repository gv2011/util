package com.github.gv2011.util.html;

public interface BlockBuilder {

  BlockBuilder close();

  BlockType blockType(String name);

  BlockBuilder setBlockType(BlockType blockType);

  BlockBuilder addText(String text);

  BlockBuilder addBlock();

  FormBuilder addForm();

}
