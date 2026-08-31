/*

    Remove-Item out -Recurse -Force -ErrorAction SilentlyContinue
    javac -d out (Get-ChildItem src -Recurse -Filter *.java).FullName
    java -cp out ysh.YSH

    to Compile / Execute and Run / Use 

*/

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
            "announce",
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
            excluding easteregss 
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
// 1827 
// 2960 
