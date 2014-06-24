package pb.osrandoms.randoms;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game.Tab;
import org.powerbot.script.rt4.Menu;

import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;

/***
 * 
 * @author Robert G
 *
 */
public class ExpRewardClaimer extends GraphScript.Action<RandomContext> {
	
	public enum Reward {
		
		ATTACK(3, 1),
		STRENGTH(4, 2),
		RANGED(5, 3),
		MAGIC(6, 4),
		DEFENCE(7, 5),
		HITPOINTS(8, 6),
		PRAYER(9, 7),
		AGILITY(10, 8),
		HERBLORE(11, 9),
		THIEVING(12, 10),
		CRAFTING(13, 11),
		RUNECRAFTING(14, 12),
		MINING(15, 13),
		SMITHING(16, 14),
		FISHING(17, 15),
		COOKING(18, 16),
		FIREMAKING(19, 17),
		WOODCUTTING(20, 18),
		FLETCHING(21, 19),
		SLAYER(22, 20),
		FARMING(23, 21),
		CONSTRUCTION(24, 22),
		HUNTER(25, 23);
		
		private final int component_id, setting_value;
		
		private Reward(int component, int settingVal) {
			this.component_id = component;
			this.setting_value = settingVal;
		}

		public int componentId() {
			return component_id;
		}

		public int settingValue() {
			return setting_value;
		}
		
	}
	
	private final int reward_widget_id = 134;
	private final int selected_reward_setting_id = 261;
	private final int exp_lamp_id = 2528;
	private final int exp_book_id = 11640;
	private final Reward default_reward = Reward.FISHING;

	private Reward selected_reward = null;

	public ExpRewardClaimer(RandomContext script) {
		super(script);
	}
	
	private Reward getReward() {
		return selected_reward == null ? default_reward : selected_reward;
	}
	
	@Override
	public void run() {
		final Component comp = ctx.widgets.widget(reward_widget_id).component(getReward().componentId());
		if (comp.valid()) {
			if (selectedReward() != getReward().settingValue()) {
				if (comp.interact("advance")) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return selectedReward() == getReward().settingValue();
						}
						
					}, 150, 10);
					return;
				}
			} else {
				final Component confirm = ctx.randomMethods.getComponentByText("confirm");
				if (confirm.valid() && confirm.interact(Menu.filter("confirm"))) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return !comp.valid();
						}
						
					}, 150, 10);
					return;
				}
			}
		} else {
			if (ctx.game.tab(Tab.INVENTORY)) {
				if (ctx.inventory.poll().click()) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return comp.valid();
						}
						
					}, 150, 10);
					return;
				}
			}
		}
	}

	private int selectedReward() {
		return ctx.varpbits.varpbit(selected_reward_setting_id);
	}

	@Override
	public boolean valid() {
		return !ctx.inventory.select().id(exp_lamp_id, exp_book_id).isEmpty();
	}

}