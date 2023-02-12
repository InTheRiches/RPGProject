package net.valor.rpgproject.utils;

/**
 * @author Projekt Valor
 * @since 2/12/2023
 */
public enum Permissions {
    RESOURCE_BAG_SIZE_21("rpgproject.resourcebag.size.21"),
    RESOURCE_BAG_SIZE_28("rpgproject.resourcebag.size.28");

    private final String permission;
    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
