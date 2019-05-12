# Hubert Hive, the UT2004 CTF Multi-Agent team
This project contains semestral project for KIV/ISW @ Universit of West Bohemia - 2018/2019

## Usage
The implementation uses the Pogamut library which provides rich API for agent controlling.

Compiled project launches with following parameters:
`HubertHive.jar YEAR TEAM BOT-SKILL BOT-COUNT HOST`
* YEAR - version of launching script - unused in Hubert HIve
* TEAM - (-1|TEAM_NUMBER) -1: equal distribution between teams 0 and 1 (red and blue)
* BOT-SKILL - Pogamut parameter for bot behavior: value between 1 and 10
* BOT-COUNT - amount of bots to control via this launch
* HOST - hostname of target server

## Structure
The core of the project, the class `cz.zcu.students.kiwi.ctf.CTFBot` is a bot controller 
which contains references to various modules defined by the Pogamut library.

This project defines several modules that are accessible from the bot as well in
same package.

The behavior and other agent logic is defined in the subpackage `behavior`, which 
contains another bot module - `BehaviorManager`. From there a certain *Personality* is
decided which specifies individual *Intents* which are to be run.

If some intents require deciding according to spawned items, the bot module
`CTFBotItemStatus` is used for navigation distance caching between logic cycles and
for specifying forbidden items.

## Behavior
The `BehaviorManager` defines three resources - Navigation, Shooting and Focus, which
can be reserved for individual intents in the logic() cycle. The idea of this decision
is to protect running intents but to allow for overwriting or in future, for temporal
pausing of intents for when, for instance, bot sees own flag lying around so that
they can pause their action, pick up the flag and resume their original action.

Currently the bot implements only simple behavior including flag stealing and securing,
item hunting or flag defending. The agent occasionally gets stuck at the ItemHoarding
intent which is yet to be fixed.

## Environment awareness
The agent solely relies on provided library resources and few map-based tweaks are due
since the generated navigation data is sometimes self-threatening.
