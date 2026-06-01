# WayGL
Make GLFW use wayland on linux systems

## It's on [Modrinth](https://modrinth.com/mod/waygl)
Heres the url: https://modrinth.com/mod/waygl

> If you are running the game inside a sandbox, e.g. by using a launcher packaged as a flatpak, make sure to allow the paths listed below to be accessed for this mod to function correctly!

<details>
<summary>Accesses Paths & Programs and why</summary>

### Features
- Option to use native GLFW binary in mod config (Available in ModMenu and `<instance>/config`)
- Injects a .desktop file and the icon in the correct locations for them to function correctly

### Accessed Paths
- `$HOME/.local/share/applications/com.mojang.minecraft.desktop` (write)
- `$HOME/.local/share/icons/hicolor/apps/ixi/minecraft.png` (write)
- `/.flatpak-info` (read, presence check, used to determine if running under flatpak)

### Accessed Programs
- `xdg-icon-resource` (to update the icon system)
- `flatpak-spawn` (to spawn xdg-icon-resource on host to update icon system if running under flatpak)
</details>


### Credits
- Moehreag (Icon Injection, go check out their wayland mod [wayland-fixes](https://github.com/moehreag/wayland-fixes))
