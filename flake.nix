{
  description = "Hello world program in ClojureScript";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-24.11";
  };

  outputs = { self, nixpkgs }: {

    packages.x86_64-linux.hello =
      with import nixpkgs {
        system = "x86_64-linux";
      };
      stdenv.mkDerivation {
        name = "hello";
        src = self;
        buildInputs = [
          clojure
        ];
        # buildPhase = "clj -M -m cljs.main --optimizations advanced -c hello-world.core";
      };

    packages.x86_64-linux.default = self.packages.x86_64-linux.hello;

  };
}
