package cn.stylefeng.guns.config;

import java.util.PriorityQueue;
import java.util.Queue;

import cn.stylefeng.guns.modular.system.entity.SellerOrder;

public class SellerOrderConfig {

	public static final Queue<SellerOrder> queue = new PriorityQueue<SellerOrder>();
	
	public static final Queue<SellerOrder> merchantQueue = new PriorityQueue<SellerOrder>();
	
	public static final void addSellerOrder(SellerOrder order) {
		if(order.getType()==1) {
			queue.add(order);
		}else {
			merchantQueue.add(order);
		}
	
	}
	
	public static final void romverSellerOrder(SellerOrder order) {
		if(order.getType() ==2) {
			if(	merchantQueue.contains(order)) {
				merchantQueue.remove(order);
			}
		}else {
			if(	queue.contains(order)) {
				queue.remove(order);
			}
		}
	
	}
	
	public static final SellerOrder pollOrder(Integer type) {
		SellerOrder  order = null;
			if(type ==2) {
				  order = merchantQueue.poll();	
				System.out.println("Processing SellerOrder with ID="+order.getSerialNo());
			}else {
				  order = queue.poll();	
				System.out.println("Processing SellerOrder with ID="+order.getSerialNo());
			}
		return order;
	}
	
}
