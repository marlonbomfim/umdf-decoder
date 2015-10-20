# Handling FIX Replay Messages #

The RawDataReporting message (see FAST specification) returned as a response to a replay request may contain more than one FAST message.  All the message data is stored in the `RawData` field, and each ApplSeqNum sequence group contains offsets and lengths that are used to index the single `RawData` block.

Extracting the data is relatively straightforward, but there are a few pitfalls to avoid.  The `RawData` field is actually a `StringField` in QuickFIX/J (because `ByteField`s are not well supported currently), so the actual raw data is stored in a `String` when extracted.  You can use `getBytes()` to extract these bytes, but you must tell the string to use the current character set (as shown in the code snippet below), which can be accessed with `CharsetSupport.getCharset()`.  Otherwise, the output bytes will be converted to Java's native character encoding, rendering the data useless.

```
// extract the bytes from the RawData StringField
byte[] rawBytes=message.getField(new RawData()).getValue().getBytes(CharsetSupport.getCharset());

// extract each FAST message
for(Group grp:message.getGroups(NoApplSeqNums.FIELD)) {
  long seqnum=Long.parseLong(grp.getField(new ApplSeqNum()).getValue());
				
  int offset=grp.getField(new RawDataOffset()).getValue();
				
  int length=grp.getField(new RawDataLength()).getValue();

  // read the FAST message's bytes into an array
  byte[] data=new byte[length];
  System.arraycopy(rawBytes, offset, data, 0, length);

  // ... do something with this message ...
}
```