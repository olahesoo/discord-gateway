function get_launch_values () {
    token = document.getElementById("token-input").value;
    guild_id = document.getElementById("guild-input").value;
    channel_id = document.getElementById("channel-input").value;
    return {"token": token,
	    "guild_id": guild_id,
	    "channel_id": channel_id}
}

function clear_emojis() {
    emojis = document.getElementById("emojis-list");
    while (emojis.firstChild) {
	emojis.removeChild(emojis.firstChild);
    }
}

function add_to_emojis (x) {
    emojis = document.getElementById("emojis-list");
    new_child = document.createElement("li");
    new_child.textContent = JSON.stringify(x);
    emojis.appendChild(new_child);
}

function launch_gateway() {
    launch_values = get_launch_values();
    socket_control = discord_gateway.core.js_launch_gateway(
	launch_values.token, launch_values.guild_id, launch_values.channel_id
    );
    discord_gateway.core.just_get_me_the_emojis_fam(socket_control, add_to_emojis);
    return socket_control;
}
