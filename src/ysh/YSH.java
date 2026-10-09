
/*

    Remove-Item out -Recurse -Force -ErrorAction SilentlyContinue
    javac -d out (Get-ChildItem src -Recurse -Filter *.java).FullName
    java -cp out ysh.YSH

    to Compile / Execute and Run / Use 

*/

// IF YOU WANT TO CONTRIBUTE TO YSH - 
// search help me and you will find comments where YSH v10 build is needed.
package ysh;

import javax.swing.SwingUtilities;
import ysh.gui.YSHWindow;

public class YSH {

     public static void main(String[] args) {
 
        SwingUtilities.invokeLater( 
            YSHWindow::new 
        );
    }
}


// ********  PLEASE IGNORE THE TEXT AFTER THIS LINE *********

/*  

    1. Im curently working on YSH v10 
    2. i can provide u all YSH versions source code ( v1 - v5 CLI , v5.GUI , v7.GUI , v8.GUI  ) and v10 is quite interesting as ive now in v10 rearranged and devided the YSH.java in 8-10 different files 
    3. LAN Networking , Easter eggs , Time sensitive behaviour (Late Night session) , Konami Pattern , multiple Themes , multiple prompt and YSH story ig 
    4. JDK 17 but older YSH like part of v7 and all v5 and CLI works even on older JDK , JAVA , JAVA Swing , JAVA AWT , JAVA IO etc 
    5. 55+ commands including easter eggs and hidden commands for fun (example 'sudo')
    6. Chat Host , multiple rooms , private and public , multiple devices , Fileshare secured and private even when its a huge LAN network , and more upcoming with SHARE for all type of data 
    7. devmode with a devmode prompt and devmode commands for testing and debugging and more and can be opened with hidden Konami pattern and upcoming Arch mode for linux users 
    8. JAR for Linux and Everyone , EXE ( for now uses 'run'/'open' using cmd but not from v10 ) for Windows and supports every os and invite only system on MAC OS until key by themselves do something with the code on GitHub
    9. Started with a dream of 15 year old to build his own OS and on a random summer vacation afternoon an idea to build a Terminl so if a OS is impossible for now he can say he "I LET THE OS RUN" and understand how a shell works by actually building one ( 20th May - first day and straight 17 hours of real programming and ended the day with YSH v5 with CLI and v5.GUI with more planning upcoming features and still running with a great new vision of uniqness and real building ) 
    10. CLI- YousufProjs-exe/YSH-CLI , GUI- YousufProjs-exe/YSH-GUI , v7- GUI- YousufProjs-exe/YSH , v8- YousufProjs-exe/YSH-v8 , v10- YousufProjs-exe/YSH-v10 (all on GitHub) {but not that well presentation} 
    11. https://yshweb.netlify.app/ is here and if u want more im here 
    12. Main project to shit on stardance is named YSH and the other one is YSH v8 Uni and we are earning 88 stardust with the "YSH" having 13hours and 3 devlogs targeting 100 stardust 
    13. better if u can see through my public stardance account : https://stardance.hackclub.com/@Yousuff/projects 
    14. Great vision having implementation and building an ecosystem around it in all directions like on web and custom apps like browser too , than just a thought and wasting time like other teens 

*/ 

/*

    1. visit https://github.com/YousufProjs-exe/YSH for v7 , https://github.com/YousufProjs-exe/YSH-CLI for all abt v1-v5 CLI , https://github.com/YousufProjs-exe/YSH-v8 for v8 details 
    2. 
            "help", 
            "mkdir", 
            "touch", 
            "rm", 
            "rename", 
            "copy", 
            "paste", 
            "move", 
            "ls", 
            "search", 
            "cat", 
            "write", 
            "cd", 
            "pwd", 
            "home", 
            "calc", 
            "echo", 
            "clear", 
            "whatsup", 
            "sysinfo", 
            "theme", 
            "open", 
            "run", 
            "check", 
            "developer", 
            "note", 
            "add", 
            "list", 
            "view", 
            "chat", 
            "scan", 
            "msg", 
            "username", 
            "listusers", 
            "kick", 
            "announce" ,
            "fileshare host", 
            "fileshare get", 
            "sudo", 
            "whoami", 
            "whocreatedyou", 
            "easteregg", 
            "exit", 
            "cls", 
            "Arch", 
            "developer" 
            and more 
            excluding eastereggs 
            comes With 
            command history 
            tab to complete 
            ctrl up and down to scroll 
            and some more shortcuts 
    3. 
    4. 
    5. It was an unreal exprence coz imagine hopping on building a Terminal/Shell with nothing like weather app , chat app but with only some cli less games in java and a broken portfolio but a <i>dream and curosity</i> like my portfolio says "Curosity , Developed". 
    6. Started with a dream of 15 year old to build his own OS and on a random summer vacation afternoon an idea to build a Terminl so if a OS is impossible for now he can say he "I LET THE OS RUN" and understand how a shell works by actually building one ( 20th May - first day and straight 17 hours of real programming and ended the day with YSH v5 with CLI and v5.GUI with more planning upcoming features and still running with a great new vision of uniqness and real building ) 
      you can more like that coz i only remebered this 

*/ 

/* 
    # YSH — Yousuf Shell

    YSH started with a simple question:
    **What if I stopped trying to imagine how an operating system works and actually built the part I could understand first?**

    What began as a small command-line experiment during a summer vacation grew into a long-running project spanning multiple versions, a GUI, a custom file system, networking, developer tools, themes, hidden features, and an entire ecosystem around it.
    YSH is a terminal/shell environment built in Java, designed to explore how shells, file systems, commands, networking, and system-like interfaces can work together.

    ## From CLI to YSH v10

    YSH has evolved through many versions:

    **CLI v1 → v2 → v3 → v4 → v5 → v5.GUI → v7 → v7.GUI → v8 → v8 Linux → v10**

    Each version represents a different stage of the project rather than simply being a version-number bump.
    The project moved from basic command handling into a GUI environment, real file-system interaction, LAN networking, multiple interfaces, developer tooling, and a much more modular architecture in v10 having its own Task Manager, File Manager, AI, and more.
    Historical versions are also available through GitHub releases so they can be run, explored, and compared independently.

    ## What can YSH do?

    YSH currently contains **55+ commands**, alongside features that go far beyond a traditional command parser:

    - Custom file-system operations
    - LAN networking
    - LAN Chat with multiple rooms
    - Public and private chat
    - Multi-device communication
    - File sharing
    - Multiple themes and prompts
    - Developer Mode with dedicated developer commands
    - Hidden features and Easter eggs
    - Konami-code based feature unlocking
    - Late Night session behaviour
    - Custom YSH story elements
    - CLI applications like Calculator and Notes systems
    - Command-line utilities and system-style tools

    -- Some features are deliberately hidden because YSH is also meant to be something you can explore rather than simply read about.

    ## The technical side

    YSH is built primarily with:

    **Java • JDK 17 • Swing • AWT • Java IO**
    and built on Intel Pentium-class hardware, with a focus on cross-platform compatibility and modularity.
    The project has grown from a much more monolithic early implementation into a modular YSH v10 architecture, with different responsibilities separated into multiple files and components.

    YSH is distributed as a JAR for general use and an EXE for Windows.

    ## Networking

    One of the major directions of YSH is networking.
    YSH includes LAN Chat functionality with multiple rooms and private/public communication between devices. File sharing is also being developed as part of the same ecosystem, with an emphasis on keeping shared data private within the LAN.
    This is one of the areas where YSH started feeling less like a collection of terminal commands and more like its own environment.

    ## Why I built it

    I originally wanted to build my own operating system.

    At the time, I didn't have the knowledge or resources to build an entire OS, so I changed the question:

    **If I can't build the whole OS yet, can I build the shell and understand what makes it work? And can say that "I Let the OS Run!"**

    That became YSH.
    My first serious YSH session happened on May 20, when I spent around 17 straight hours programming and reached YSH v5 CLI and v5.GUI.
    Since then, the project has kept growing.
    YSH isn't finished, and that's intentional.
    There are still planned features such as improved file sharing, SHARE functionality, Arch mode, additional developer features, and further work toward the wider YSH ecosystem.

    ## YSH is bigger than a terminal

    YSH is becoming the foundation of something larger.
    The long-term idea is to build an ecosystem of custom software around YSH — from the shell itself to web projects, custom applications, a browser, and eventually **YSH One**, a small device designed around the project.

    For me, YSH is less about making another terminal and more about learning how far I can take an idea when I keep rebuilding it instead of abandoning it.
    **Started as a shell. Still becoming something bigger.**

*/ 

// 1827 
// 2960 

/* 

    # YSH — Yousuf Shell Terminal

    YSH started with a simple goal: understand how operating systems work by building the part I could reach first — a Shell.
    What began as a CLI project grew through **v1 → v2 → v3 → v4 → v5 → v5.GUI → v7 → v7.GUI → v8 → v8 Linux → v10**.
    YSH is now a Java-based terminal environment with **55+ commands**, GUI, file-system operations, LAN Chat, multi-device networking, file sharing, themes, developer mode, hidden features, Easter eggs and more.
    Built with Java, JDK 17, Swing, AWT and Java IO on Intel Pentium.
    YSH is still evolving toward a larger ecosystem of software and hardware.
    **Started as a shell. Still becoming something bigger.**

*/ 


/* 

Power Shell cursor
old networking commands (improve filehost)
cmd default theme 
COMPLETE TERMINAL WORKING (to some level not something like clone of cmd)
use of ASCII art 
mini cli game too 
-- heavy dicision -> v10 would be v5.CLI of YSH series and the following versions would be industrial level polished to be displayed like hows v8 for now
Task Manager and tiny local space reservation for achievements collections 
   - achievements 
   konami patterns
   developer mode
   Arch mode
   - easter eggs
   late night session
   too much on LAN
   and somme more like this to add
new system for run/open as currently it uses cmd which isnt cross platform
promotting YSH ecosystem and and its developer 
some library thing for installation of packages of dev tools like other shells can 
more customization as we currently provide only 6 themes but just 2 possible prompts so an option to customize late session msg , prompt , cursor and theme if possible 
including our traind SLM and then LLM if we can or better to actully include as when user types  AI: who r u and YSH answers YSH... but same command without "AI:" prefix and it should be able to answer like a LLM 

and giving YSH Legacy the REST for some time till we need it for several thing or OS and YSH One 

*/ 

// start at 02:04am while Hackatime is 3h 26m 
// so 5min will be 02:10am while Hackatime will be 3h 35m 

// custom cursor 
// Arch mode 
// and mode fixes 
// solved banner and promt print conflicts 

// themes - translu and obe , pin and oran and magen  
// music - that discord 
// CLI too - huge 
// Software Library - atleast nes essentials and utilitiies 
// Tuffware 
// YSH One - Raspbery pi zero 2 w 
// KeyKap - ESP32 C3 Mini 

// https://hackpad.hackclub.com/guide  for Hakin Pad 
