<h1 style="text-align:center;">Figura Backend Server</h1>

#### Currently, Figura lacks a "plug-and-play" solution for backend server support. This plugin aims to address that gap, providing essential features and functionalities for server-side operations.

### What's Included?
- **Avatar Management**: Efficiently organize avatars into folders based on UUIDs. The directory structure is as follows: `plugins/FiguraWebServer/avatars/[first two of uuid]/[UUID]`. This makes it easy to manage and locate avatars.

- **NPC Support**: Integrates non-player characters (NPCs) by utilizing UUIDs within the avatar folder. This feature allows for dynamic interactions and customization of NPC behaviors and appearances.

- **HTTPS and ACME Challenges**: Offers built-in support for HTTPS certificates, ensuring secure connections. The plugin handles ACME challenges to automate the process of obtaining and renewing certificates, enhancing security and user trust.

- **Customizable Server Configuration**: Allows users to customize server settings to suit their specific needs, including security protocols, data management, and user access controls.

This plugin serves as a comprehensive solution for those looking to enhance their Figura experience with backend server capabilities.

**Check out the Figura here:** [https://github.com/FiguraMC/Figura](https://github.com/FiguraMC/Figura)


# FAQ

### • How can I create Avatars?
> Many avatars are created with [Blockbench](https://www.blockbench.net/) and scripted with [Lua](https://lua.org/). I won't teach you how to create avatars, as this isn't the place for that. However people over at the [Official Figura Discord](https://discord.figuramc.org/) server, will gladly help you out with any troubles to face.

### • Where can I find Avatars to download?
> For now, you can find Avatars in the showcase channel in the [Official Figura Discord](https://discord.figuramc.org/) server (A Web Based and In-Game browser is in the works!)

### • My Minecraft is cracked (non-premium/non-original) or I'm trying to join a cracked offline mode server, why can't I use Figura?
> Figura uses your account's UUID and your Mojang authentication as a way to prove you own that account, avoiding unwanted / malicious uploads
> 
> Non-premium Minecraft accounts don't authenticate with Mojang, and Offline mode servers don't report working UUID's, as such can neither upload nor download Figura avatars

*(More of these FAQs can be found on their [GitHub](https://github.com/FiguraMC/Figura?tab=readme-ov-file#faq))*