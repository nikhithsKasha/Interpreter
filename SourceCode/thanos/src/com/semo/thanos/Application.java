package com.semo.thanos;

import java.util.List;

public class Application {

    public static void main(String[] args) {

       if(args !=null && args.length>0) {
            List<String> file = Tokenizer.readFile(args[0]);
            Parser.parseTokens(file, new Values());
        }else{
            System.out.println("please provide a valid path");
        }
    }
}
