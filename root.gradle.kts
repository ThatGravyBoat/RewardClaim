plugins {
    kotlin("jvm") version "1.9.24" apply false
    id("net.kyori.blossom") version "1.3.2" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    "1.21-fabric"(12100, "yarn") {
        "1.20.1-fabric"(12001, "yarn") {
            "1.19-fabric"(11900, "yarn") {
                "1.18.1-fabric"(11801, "yarn") {
                    "1.17.1-fabric"(11701, "yarn") {
                        "1.17.1-forge"(11701, "srg") {
                            "1.12.2-forge"(11202, "srg") {
                                "1.8.9-forge"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
                            }
                        }
                    }
                }
            }
        }
    }
}