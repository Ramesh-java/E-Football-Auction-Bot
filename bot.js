const { default: makeWASocket, useMultiFileAuthState, DisconnectReason, fetchLatestBaileysVersion } = require('@whiskeysockets/baileys');
const { Boom } = require('@hapi/boom');
const qrcode = require('qrcode-terminal');

async function startBot() {
  const { state, saveCreds } = await useMultiFileAuthState('auth');
  const { version } = await fetchLatestBaileysVersion();

  const sock = makeWASocket({
    version,
    auth: state,
    printQRInTerminal: false,
    browser: ['Ubuntu', 'Chrome', '22.04.4'],
  });

  sock.ev.on('connection.update', async (update) => {
    const { connection, lastDisconnect, qr } = update;

    if (qr) {
      console.log("📲 Scan this QR:");
      qrcode.generate(qr, { small: true });
    }

    if (connection === 'close') {
      const statusCode = lastDisconnect?.error instanceof Boom
        ? lastDisconnect.error.output.statusCode
        : lastDisconnect?.error?.output?.statusCode;
      const shouldReconnect = statusCode !== DisconnectReason.loggedOut;

      console.log("🔁 Disconnected. Status:", statusCode, "Should reconnect?", shouldReconnect);
      if (shouldReconnect) {
        setTimeout(() => startBot(), 1500);
      } else {
        console.log("❌ Logged out. Please delete 'auth' and scan again.");
      }
    } else if (connection === 'open') {
      console.log("✅ Connected to WhatsApp");
    }
  });

  sock.ev.on('creds.update', saveCreds);

  const axios = require('axios');

  sock.ev.on('messages.upsert', async ({ messages }) => {
    const msg = messages[0];
    if (!msg.message?.conversation) return;

    const text = msg.message.conversation.trim();

    if (text.startsWith("!sell")) {

    const parts = text.split(" ");
    const name = parts[1];
    const price = parseInt(parts[2].substring(0,parts[2].length-1));//1
    const team = parts.slice(4).join(" ");
    const epic = false;

    if (!name || isNaN(price) || !team) {
      await sock.sendMessage(msg.key.remoteJid, {
        text: `❌ Usage: !sell <player> <price> <team>`
      });
      return;
    }
    try{
      const res=await axios.post('http://localhost:8080/auction/bid',{
        name,
        price,
        team,
        epic
      });

      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content|| `✅ ${name} sold to ${team} for ${price}M`
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:  `❌ Failed to process sale.`
      });
      console.error(err);
    }
  }if(text.startsWith("!balance")){
    const parts=text.split(" ");
    var team = "";
    if(parts.length==2){
      team = parts[1];
    }else{
      team = parts.slice(1).join(" ");
    }

    try{
      const res=await axios.get("http://localhost:8080/auction/balance?team="+team);
     

    await sock.sendMessage(msg.key.remoteJid,{
      text:res.data.content||'No content'
    });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text: `❌ Failed to Get Balance of ${team}.`
      });
      console.error(err);
    }
  }if(text.startsWith("!allBalance")){
    try{
      const result=await axios.get("http://localhost:8080/auction/getAllBalance")
      await sock.sendMessage(msg.key.remoteJid,{
      text:result.data.content
    });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Get Everyone Balance'
      });
    }
    
  }if(text.startsWith("!rollback")){
    try{
      const result=await axios.get("http://localhost:8080/auction/rollback")

      await sock.sendMessage(msg.key.remoteJid,{
        text:result.data.content
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Rollback'
      });
    }
  }
  if(text.startsWith("!view")){
    const parts=text.split(" ");
    var team = "";
    if(parts.length==2){
      team = parts[1];
    }else{
      team = parts.slice(1).join(" ");
    }
  
    try{
      const result=await axios.get("http://localhost:8080/auction/getPlayers/"+team);

      await sock.sendMessage(msg.key.remoteJid,{
        text:result.data.content
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Get Team Info'
      });
    }
  }

  if(text.startsWith("!commands")){
    try{
      const result=await axios.get("http://localhost:8080/auction/allCommands");

      await sock.sendMessage(msg.key.remoteJid,{
        text:result.data.content
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Get Commands'
      });
    }
  }

  if(text.startsWith("!next")){//
    try{
      const result=await axios.get("http://localhost:8080/auction/next");

      await sock.sendMessage(msg.key.remoteJid,{
        text:result.data.content
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Get Next Player'
      });
    }
  }

  if(text.startsWith("!addAll")){
    const parts=text.split("\n");
    try{
      const res=await axios.post("http://localhost:8080/auction/addAll",{
        items:parts
      });
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:'❌ Failed to Add All Players'
      });
    }
  }

  if(text.startsWith("!unsold")){//!unsold name
    const players=text.split(" ");
    const name=players[1];//name
    try{
      const res=await axios.get("http://localhost:8080/auction/unsold/"+name);
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:' Failed to Mark Unsold'
      });
    }
  }

  if(text.startsWith("!getUnsold")){
    try{
      const res=await axios.get("http://localhost:8080/auction/getUnsold");
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:' Failed to get Unsold Auction'
      });
    }
  }

  if (text.startsWith("!epic")) {

    const parts = text.split(" ");
    const name = parts[1];
    const price = parseInt(parts[2].substring(0,parts[2].length-1));//1
    const team = parts.slice(4).join(" ");
    const epic = true;

    if (!name || isNaN(price) || !team) {
      await sock.sendMessage(msg.key.remoteJid, {
        text: `❌ Usage: !sell <player> <price> <team>`
      });
      return;
    }
    try{
      const res=await axios.post('http://localhost:8080/auction/bid',{
        name,
        price,
        team,
        epic
      });

      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content|| `✅ ${name} sold to ${team} for ${price}M`
      });
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:  `❌ Failed to process sale.`
      });
      console.error(err);
    }
  }


 

  if(text.startsWith("!delete")){
    const parts=text.split(" ");
    const name=parts.slice(1).join(" ").trim();//trim-> removes leading and trailing whitespace
    try{
      const res=await axios.get("http://localhost:8080/auction/delete/"+name);
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:' Failed to Delete Player'
      });
  }
  }

  if(text.startsWith("!eMinus")){//!eMinus sh
    const team=text.split(" ").slice(1).join(" ").trim();//trim-> removes 
    try{
      const res=await axios.get("http://localhost:8080/auction/epicCountMinus/"+team);
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:' Failed to Get Epic Count'
      });
    }

  }
  if(text.startsWith("!ePlus")){//!eMinus sh
    const team=text.split(" ").slice(1).join(" ");//
    try{
      const res=await axios.get("http://localhost:8080/auction/epicCountPlus/"+team);
      await sock.sendMessage(msg.key.remoteJid,{
        text:res.data.content
      })
    }catch(err){
      await sock.sendMessage(msg.key.remoteJid,{
        text:' Failed to Get Epic Count'
      });
    }

  }

  if(!text.startsWith("!rollback") || !text.startsWith("!balance")||!text.startsWith("!allBalance")||!text.startsWith("!sell")){
    return;
  }

    /*await sock.sendMessage(msg.key.remoteJid, {
      text: `✅ ${player} sold to ${team} for ${price}M`
    });*/
  });
}

startBot();
