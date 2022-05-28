package com.semo.thanos;

import java.util.List;

public class PrintThanos {

    public static void main(String[] args) {
        List<String> file = Tokenizer.readFile("C:\\Users\\admin\\Desktop\\thanos (1)\\thanos\\src\\resources\\print.thanos");
        Parser.parseTokens(file, new Values());
    }
}