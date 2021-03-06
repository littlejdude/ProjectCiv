package main.java.projectciv.hex;

import java.util.ArrayList;
import java.util.List;

import main.java.projectciv.Main;
import main.java.projectciv.client.Camera;
import main.java.projectciv.hex.HexData.HexType;
import main.java.projectciv.util.CollectionUtils;
import main.java.projectciv.util.EnumDirection;
import main.java.projectciv.util.math.Vec2i;
import main.java.projectciv.util.math.Vec3i;

public class HexHandler {
	private List<Hex> hexes = new ArrayList<Hex>(), visibleHexesCache = new ArrayList<Hex>();
	public static final int RADIUS = 25;
	
	public void setup() {
		for (int zz = -RADIUS; zz <= RADIUS; zz++) {
			for (int xx = -RADIUS; xx <= RADIUS; xx++) {
				if (xx + zz <= RADIUS && -(xx + zz) <= RADIUS) {
					hexes.add(new Hex(new Vec3i(xx, -(xx + zz), zz)));
				}
			}
		}
		
		List<Hex> newOceanHex = new ArrayList<Hex>();
		
		for (Hex hex : hexes) {
			if (hex.getHexData().getHexType() == HexType.water) {
				int count = 0;
				for (EnumDirection dir : EnumDirection.values()) {
					Hex hh = HexUtils.getHexAt(hex.getHexPos(), dir);
					if (hh != null && hh.getHexData().getHexType() != HexType.water) {
						count++;
					}
				}
				
				if (count <= 1) {
					newOceanHex.add(hex);
				}
			}
		}
		
		for (Hex hex : newOceanHex) {
			hex.setHexData(new HexData(HexType.ocean));
		}
	}
	
	public void checkHexClicked(Vec2i mousePos) {
		for (Hex hex : visibleHexesCache) {
			if (hex.intersects(mousePos.getX(), mousePos.getY(), 1, 1)) {
				Main.getMain().getCityHandler().getCities().get(0).buyHex(hex.getHexPos());
				break;
			}
		}
	}
	
	public void resetVisibleHexCache() {
		visibleHexesCache.clear();
	}
	
	public Hex getHex(Vec3i vec) {
		for (Hex hex : hexes) {
			if (hex.getHexPos().equals(vec)) {
				return hex;
			}
		}
		
		return null;
	}
	
	public List<Hex> getHexes() {
		return CollectionUtils.unmodifiableCopyList(hexes);
	}
	
	/** returns a copied list */
	public List<Hex> getVisibleHexes() {
		if (visibleHexesCache.isEmpty()) {
			Camera cam = Main.getMain().getCamera();
			for (Hex hex : hexes) {
				if (hex.intersects(cam.getBounds())) {
					visibleHexesCache.add(hex);
				}
			}
		}
		
		return CollectionUtils.unmodifiableCopyList(visibleHexesCache);
	}
}
