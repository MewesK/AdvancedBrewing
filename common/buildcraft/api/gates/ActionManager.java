package buildcraft.api.gates;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.transport.IPipe;

public class ActionManager {

	public static Map<String, ITrigger> triggers = new HashMap<String, ITrigger>();
	public static Map<String, IAction> actions = new HashMap<String, IAction>();
	private static LinkedList<ITriggerProvider> triggerProviders = new LinkedList<ITriggerProvider>();
	private static LinkedList<IActionProvider> actionProviders = new LinkedList<IActionProvider>();

	public static void registerTriggerProvider(ITriggerProvider provider) {
		if (provider != null && !ActionManager.triggerProviders.contains(provider)) {
			ActionManager.triggerProviders.add(provider);
		}
	}

	public static void registerTrigger(ITrigger trigger) {
		ActionManager.triggers.put(trigger.getUniqueTag(), trigger);
	}

	public static void registerAction(IAction action) {
		ActionManager.actions.put(action.getUniqueTag(), action);
	}

	public static LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity entity) {
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

		for (ITriggerProvider provider : ActionManager.triggerProviders) {
			LinkedList<ITrigger> toAdd = provider.getNeighborTriggers(block, entity);

			if (toAdd != null) {
				for (ITrigger t : toAdd) {
					if (!triggers.contains(t)) {
						triggers.add(t);
					}
				}
			}
		}

		return triggers;
	}

	public static void registerActionProvider(IActionProvider provider) {
		if (provider != null && !ActionManager.actionProviders.contains(provider)) {
			ActionManager.actionProviders.add(provider);
		}
	}

	public static LinkedList<IAction> getNeighborActions(Block block, TileEntity entity) {
		LinkedList<IAction> actions = new LinkedList<IAction>();

		for (IActionProvider provider : ActionManager.actionProviders) {
			LinkedList<IAction> toAdd = provider.getNeighborActions(block, entity);

			if (toAdd != null) {
				for (IAction t : toAdd) {
					if (!actions.contains(t)) {
						actions.add(t);
					}
				}
			}
		}

		return actions;
	}

	public static LinkedList<ITrigger> getPipeTriggers(IPipe pipe) {
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

		for (ITriggerProvider provider : ActionManager.triggerProviders) {
			LinkedList<ITrigger> toAdd = provider.getPipeTriggers(pipe);

			if (toAdd != null) {
				for (ITrigger t : toAdd) {
					if (!triggers.contains(t)) {
						triggers.add(t);
					}
				}
			}
		}

		return triggers;
	}

	public static ITrigger getTriggerFromLegacyId(int legacyId) {
		for (ITrigger trigger : ActionManager.triggers.values()) {
			if (trigger.getLegacyId() == legacyId) {
				return trigger;
			}
		}
		return null;
	}

	public static IAction getActionFromLegacyId(int legacyId) {
		for (IAction action : ActionManager.actions.values()) {
			if (action.getLegacyId() == legacyId) {
				return action;
			}
		}
		return null;
	}
}
