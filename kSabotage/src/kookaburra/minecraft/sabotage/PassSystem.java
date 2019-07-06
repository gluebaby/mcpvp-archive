
package kookaburra.minecraft.sabotage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import kookaburra.minecraft.mcpvp.McpvpPlayer;
import kookaburra.minecraft.mcpvp.Rank;

public class PassSystem
{
	public static ArrayList<McpvpPlayer> passQueue = new ArrayList<McpvpPlayer>();
	public static HashMap<McpvpPlayer, Boolean> selected = new HashMap<McpvpPlayer, Boolean>();

	static void test()
	{
		McpvpPlayer rand = new McpvpPlayer("A");
		rand.setRank(Rank.PRO);
		rand.setHasPasses(true);
		addPlayer(rand);
		rand = new McpvpPlayer("B");
		rand.setRank(Rank.NORMAL);
		rand.setHasPasses(true);
		addPlayer(rand);
		rand = new McpvpPlayer("C");
		rand.setRank(Rank.NUB);
		rand.setHasPasses(true);
		addPlayer(rand);
		rand = new McpvpPlayer("D");
		rand.setRank(Rank.PRO);
		rand.setHasPasses(false);
		addPlayer(rand);
		for (int i = 0; i < 3; i++)
		{
			McpvpPlayer player = getNext();
			System.out.println(player.getRank());
			System.out.println(player.hasPasses());
			System.out.println("---------------------");
		}
	}

	public static void addPlayer(McpvpPlayer player)
	{
		passQueue.add(player);
	}

	public static McpvpPlayer getNext()
	{
		Collections.sort(passQueue, new Comparator<McpvpPlayer>()
		{
			@Override
			public int compare(McpvpPlayer o1, McpvpPlayer o2)
			{
				// System.out.println(o1.getRank());
				// System.out.println(o1.hasPasses());
				// System.out.println(o2.getRank());
				// System.out.println(o2.hasPasses());
				if (o1.getRank().ordinal() > o2.getRank().ordinal())
				{
					// System.out.println("Higher rank then o2");
					if (!o1.hasPasses() && o2.hasPasses())
					{
						// System.out.println("1");
						return 1;
					}
					else
					{
						// System.out.println("-1");
						return -1;
					}
				}
				else if (o1.getRank().ordinal() == o2.getRank().ordinal())
				{
					// System.out.println("Equal rank");
					if (!o1.hasPasses() && o2.hasPasses())
					{
						// System.out.println("1");
						return 1;
					}
					else if (o1.hasPasses() && !o2.hasPasses())
					{
						// System.out.println("-1");
						return -1;
					}
					else
					{
						// System.out.println("0");
						return 0; // Both have passes, both have the same rank
					}
				}
				else if (o1.getRank().ordinal() < o2.getRank().ordinal())
				{
					// System.out.println("Lower rank then o2");
					if (o1.hasPasses() && !o2.hasPasses())
					{
						// System.out.println("-1");
						return -1;
					}
					else
					{
						// System.out.println("1");
						return 1;
					}
				}
				return 0;
			}
		});
		int index = 0;
		for (int i = 0; i < passQueue.size(); i++)
		{
			McpvpPlayer player = passQueue.get(i);
			if (player != null)
			{
				index = i;
				break;
			}
		}
		McpvpPlayer next = passQueue.get(index);
		if (next.getRank().equals(Rank.PRO) && passQueue.size() + selected.size() > 3)
		{
			if (!next.hasPasses())
			{
				passQueue.remove(index);
				return getNext(); // If they have no passes, and the queue has 3
									// others that do have passes, then remove
									// them and pick the next.
			}
			else
			{
				selected.put(next, true); // Since PRO is the lowest priority
											// without a pass, only if there's
											// nobody else
			}
		}
		else if (next.getRank().equals(Rank.PRO) && passQueue.size() + selected.size() <= 3)
		{
			selected.put(next, false);
		}
		else
		{
			selected.put(next, true);
		}
		passQueue.remove(index);
		return next;
	}
}
