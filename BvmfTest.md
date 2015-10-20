# BVMF Console #

The BVMF console module is a very simple test of the features available in the API.
Once you have the source checked out, you must perform some extra steps to get it working:

  * _Get the FAST template file_
> You can download a copy of the BVMF FAST templates from
> [ftp://ftp.bmf.com.br/FIXFAST/templates/Production](ftp://ftp.bmf.com.br/FIXFAST/templates/Production)
> like so:

> `$ curl ftp://ftp.bmf.com.br/FIXFAST/templates/Production/templates-UMDF.xml -# -o templates-UMDF.xml `

  * _Set up replay connection session file_
> You need to create a session configuration file if you want to connect to the FIX replay stream.
> OpenUMDF uses QuickFIX to connect to the FIX session, so you can follow the information there:
> http://www.quickfixengine.org/quickfixj/doc/usermanual/usage/configuration.html

> Example FIX session file with a single session defined:
```
[DEFAULT]
ConnectionType=initiator
ReconnectInterval=60
SenderCompID=BROKER
BeginString=FIXT.1.1
DataDictionary=bvmf_replay.xml
HeartBtInt=30
StartTime=00:00:00
EndTime=00:00:00
DefaultApplVerID=7
FileLogPath=bvmf-session.log

[SESSION]
TargetCompID=CHANNEL1
SocketConnectPort=30000
SocketConnectHost=10.10.10.2

```

  * _Set up UMDF configuration file_
> Last, you need to create a configuration file for the BVMF console application itself.
> This file defines all the channels you wish to listen on and some other configuration values.

> The [FIX](FIX.md) section defines information related to the FIX replay session.
> Currently you can specify `ReplaySessionFile` which should be a path to your QuickFix session configuration file above.  The default value is `replay_session.cfg`

> The [FAST](FAST.md) section allows you to specify `TemplateFile`, which should be a path to your FAST template file (see above).  The default value is `templates-UMDF.xml`

> All other section names are treated as channel definitions.  In these sections, you can define the following:
    * `ChannelID` - BVMF channel identification number
    * `Name` - Channel name (used only for convenience)
    * `InstrumentDefinitionIP` - Instrument definition feed UDP multicast address
    * `InstrumentDefinitionPort` - Instrument definition feed port
    * `IncrementalsIP` - Incremental feed UDP multicast address
    * `IncrementalsPort` - Incremental feed port
    * `MarketRecoveryIP` - Market Recovery feed UDP multicast address
    * `MarketRecoveryPort` - Market Recovery feed port
    * `SenderCompID` - Your `SenderCompID` from FIX session settings file
    * `TargetCompID` - Channel's `TargetCompID` - this must match the value from the FIX session settings file
    * `BeginString` - Currently, should always be `FIXT.1.1`

> Example configuration file that defines one channel:
```
[FAST]
TemplateFile=templates-UMDF.xml

[FIX]
ReplaySessionFile=replay_session.cfg

[CHANNEL001]
ChannelID=001
Name=Channel 1 (001)

InstrumentDefinitionIP=10.0.0.1
InstrumentDefinitionPort=10000

IncrementalsIP=10.0.0.2
IncrementalsPort=10000

MarketRecoveryIP=10.0.0.2
MarketRecoveryPort=20000

SenderCompID=BROKER
TargetCompID=CHANNEL1
BeginString=FIXT.1.1
```

  * _Run Application_
> By default, the application looks in the current working directory under conf/ for its configuration files.  You can override this by setting the `conf` system property when executing the JVM (e.g., `-Dconf=/home/fred/bvmf-conf`)

> To set this value in Eclipse, go to Run -> Run Configurations... and create a new configuration for the bvmf.umdf\_console.Main class if needed.  Under the Arguments tab, add -Dconf=whatever to the VM arguments box.