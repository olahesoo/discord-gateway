{
  description = "Discord gateway API connector in ClojureScript";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-24.11";

    cljs = {
      url = "https://github.com/clojure/clojurescript/releases/download/r1.11.132/cljs.jar";
      flake = false;
    };
    core-async = {
      url = "https://repo1.maven.org/maven2/org/clojure/core.async/1.7.701/core.async-1.7.701.jar";
      flake = false;
    };
    transit-js = {
      url = "https://repo1.maven.org/maven2/com/cognitect/transit-js/0.8.874/transit-js-0.8.874.jar";
      flake = false;
    };
    transit-cljs = {
      url = "https://repo1.maven.org/maven2/com/cognitect/transit-cljs/0.8.280/transit-cljs-0.8.280.jar";
      flake = false;
    };
  };

  outputs = { self, nixpkgs, cljs, core-async, transit-js, transit-cljs }: {
    packages.x86_64-linux.default = self.packages.x86_64-linux.compile-main;

    packages.x86_64-linux.compile-main =
      with import nixpkgs {
        system = "x86_64-linux";
      };
      stdenv.mkDerivation {
        name = "discord-gateway";
        src = self;
        buildInputs = [
          jdk23_headless
        ];
        buildPhase = ''
        runHook preBuild
        mkdir deps
        cp ${cljs} deps/cljs.jar
        cp ${core-async} deps/core-async.jar
        cp ${transit-js} deps/transit-js.jar
        cp ${transit-cljs} deps/transit-cljs.jar
        java -classpath "deps/*:src" cljs.main --optimizations advanced --compile discord-gateway.core
        mv out/main.js $out
        runHook postBuild
        '';
      };
  };
}
