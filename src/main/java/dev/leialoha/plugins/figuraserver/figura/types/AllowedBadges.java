package dev.leialoha.plugins.figuraserver.figura.types;

import java.util.ArrayList;

public class AllowedBadges {
    public int[] pride = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    public int[] special = new int[] { 0, 0, 0, 0, 0, 0 };

    public final void setBadge(Special badge, boolean active) {
        setBadge(badge.id, active, false);
    }

    public final void setBadge(Pride badge, boolean active) {
        setBadge(badge.id, active, false);
    }

    public final void setBadge(int id, boolean active, boolean isPride) {
        try {
            int[] tempFlags = isPride ? this.pride : this.special;
            int[] flags = new ArrayList<Integer>() {{
                for (int flag: tempFlags) this.add(flag);
                this.set(id, active ? 1 : 0);
            }}.stream().mapToInt(Integer::intValue).toArray();

            if (isPride) this.pride = flags;
            else this.special = flags;
        } catch (IndexOutOfBoundsException ignored) {}
    }

    public final void clearBadges() {
        clearPrideBadges();
        clearSpecialBadges();
    }

    public final void clearPrideBadges() {
        this.pride = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }

    public final void clearSpecialBadges() {
        this.special = new int[] { 0, 0, 0, 0, 0, 0 };
    }

    public enum Pride {
        AGENDER, AROACE, AROMANTIC, ASEXUAL, BIGENDER, BISEXUAL, DEMIBOY,
        DEMIGENDER, DEMIGIRL, DEMIROMANTIC, DEMISEXUAL, DISABILITY, FINSEXUAL, GAYMEN,
        GENDERFAE, GENDERFLUID, GENDERQUEER, INTERSEX, LESBIAN, NONBINARY, PANSEXUAL,
        PLURAL, POLYSEXUAL, PRIDE, TRANSGENDER;

        private final int id;

        Pride() {
            this.id = ordinal();
        }
    }

    public enum Special {
        DEV, DISCORD_STAFF, CONTEST,
        DONATOR, TRANSLATOR, IMMORTALIZED;

        private final int id;

        Special() {
            this.id = ordinal();
        }
    }
}
