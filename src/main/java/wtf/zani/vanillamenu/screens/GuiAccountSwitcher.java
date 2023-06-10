package wtf.zani.vanillamenu.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import wtf.zani.vanillamenu.accessors.Accessor;
import wtf.zani.vanillamenu.accessors.lunar.AccountAccessor;
import wtf.zani.vanillamenu.accessors.lunar.AccountManagerAccessor;

import java.util.Map;

public class GuiAccountSwitcher extends GuiScreen {
    private final GuiScreen previousScreen = Minecraft.getMinecraft().currentScreen;
    private final Map<String, AccountAccessor> accounts = Accessor.accountAccessor.getAccounts();

    private AccountList list;

    @Override
    public void initGui() {
        if (!this.accounts.isEmpty()) {
            this.list = new AccountList(Minecraft.getMinecraft());
            this.list.registerScrollButtons(7, 8);
        }

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 35, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == 0) {
            this.mc.displayGuiScreen(this.previousScreen);

            this.previousScreen.initGui();
        } else if (this.list != null) {
            this.list.actionPerformed(guiButton);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (this.list != null) this.list.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (this.list != null) {
            this.list.drawScreen(mouseX, mouseY, partialTicks);
        } else {
            this.drawCenteredString(this.fontRendererObj, "You have no accounts! Launch Lunar without Vanilla Menu to add an account", this.width / 2, this.height / 2, 0xFFA0A0);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private class AccountList extends GuiSlot {
        private final AccountManagerAccessor accountManager = Accessor.accountAccessor;
        private final Map<String, AccountAccessor> accounts = GuiAccountSwitcher.this.accounts;

        private AccountAccessor currentAccount = this.accountManager.getCurrentAccount();

        public AccountList(Minecraft minecraft) {
            super(minecraft, GuiAccountSwitcher.this.width, GuiAccountSwitcher.this.height, 32, GuiAccountSwitcher.this.height - 65 + 4, 20);
        }

        @Override
        protected int getSize() {
            return this.accounts.size();
        }

        @Override
        protected void elementClicked(int id, boolean isDoubleClick, int mouseX, int mouseY) {
            final String uuid = (String) this.accounts.keySet().toArray()[id];
            final AccountAccessor account = this.accounts.get(uuid);

            this.accountManager.setAccount(account);
            this.currentAccount = account;
        }

        @Override
        protected int getContentHeight() {
            return this.getSize() * this.slotHeight;
        }

        @Override
        protected boolean isSelected(int id) {
            final String uuid = (String) this.accounts.keySet().toArray()[id];

            return this.currentAccount.getUsername().equals(this.accounts.get(uuid).getUsername());
        }

        @Override
        protected void drawBackground() {
            GuiAccountSwitcher.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int id, int unused, int y, int unused2, int mouseXIn, int mouseYIn) {
            final FontRenderer fontRenderer = GuiAccountSwitcher.this.fontRendererObj;

            final String uuid = (String) this.accounts.keySet().toArray()[id];
            final AccountAccessor account = this.accounts.get(uuid);

            GuiAccountSwitcher.this.drawCenteredString(
                    fontRenderer,
                    account.getUsername(),
                    this.width / 2,
                    y + 4,
                    mouseYIn > y && mouseYIn < y + this.slotHeight
                            ? 0xFFFF55 : -1);
        }
    }
}
