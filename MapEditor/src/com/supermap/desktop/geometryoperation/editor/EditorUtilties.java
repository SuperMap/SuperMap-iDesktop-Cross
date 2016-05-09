package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.desktop.Application;

public class EditorUtilties {

	/**
	 * 获取点到线的最短距离以及对应的垂足（端点）坐标
	 * 
	 * @param breakPoint
	 * @param desLinePoints
	 * @param tolerance
	 * @param Point2D
	 * @return
	 */
	static double getMinDistance(Point2D breakPoint, Point2Ds desLinePoints, Double tolerance, Point2D perpendicularFoot, int nSegment) {
		double dDistance = 0;
		try {
			if (desLinePoints != null) {
				int nDesPointCount = desLinePoints.getCount();
				double dTempDistance = 0;
				Point2D pntTemp = Point2D.getEMPTY();
				for (int i = 0; i < nDesPointCount - 1; i++) {

					// 会出现起始点和终点重合的情况，这里做一下处理
					pntTemp = Geometrist.computePerpendicularPosition(breakPoint, desLinePoints.getItem(i), desLinePoints.getItem(i + 1));

					// 如果垂足在线段上，取点到垂足的距离
					if (pntTemp != Point2D.getEMPTY() && isPointInLineRect(pntTemp, desLinePoints.getItem(i), desLinePoints.getItem(i + 1), tolerance))
					// if (Geometrist.IsPointOnLine(perpendicularFoot, desLinePoints[i], desLinePoints[i + 1], false))
					{
						double dOffsetX = pntTemp.getX() - breakPoint.getX();
						double dOffsetY = pntTemp.getY() - breakPoint.getY();
						dTempDistance = Math.sqrt(dOffsetX * dOffsetX + dOffsetY * dOffsetY);
						if (i == 0) {
							dDistance = dTempDistance;
							perpendicularFoot = pntTemp;
							nSegment = i;
						}
					}
					// 如果垂足在延长线上，则取点到线的两个端点的最小距离，否则取点到垂足的距离
					else {
						double dOffsetX = desLinePoints.getItem(i).getX() - breakPoint.getX();
						double dOffsetY = desLinePoints.getItem(i).getY() - breakPoint.getY();
						dTempDistance = Math.sqrt(dOffsetX * dOffsetX + dOffsetY * dOffsetY);
						pntTemp = desLinePoints.getItem(i);
						if (i == 0) {
							dDistance = dTempDistance;
							nSegment = i;
						}

						if (i == nDesPointCount - 2) // 最后一段
						{
							dOffsetX = desLinePoints.getItem(i + 1).getX() - breakPoint.getX();
							dOffsetY = desLinePoints.getItem(i + 1).getY() - breakPoint.getY();
							double dTempDistance2 = Math.sqrt(dOffsetX * dOffsetX + dOffsetY * dOffsetY);
							if (dTempDistance > dTempDistance2) {
								dTempDistance = dTempDistance2;
								pntTemp = desLinePoints.getItem(i + 1);
								i++;
							}
						}
					}

					if (dDistance > dTempDistance) // 找到更小的距离后再次赋值
					{
						dDistance = dTempDistance;
						perpendicularFoot = pntTemp;
						nSegment = i;
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return dDistance;
	}

	/**
	 * 判断点是否在线段的外接矩形内
	 * 
	 * @param point
	 * @param pntStart
	 * @param pntEnd
	 * @param tolerance
	 * @return
	 */
	static boolean isPointInLineRect(Point2D point, Point2D pntStart, Point2D pntEnd, double tolerance) {
		Boolean bResult = false;
		try {
			if (point != Point2D.getEMPTY() && point.getX() >= Math.min(pntStart.getX(), pntEnd.getX()) - tolerance
					&& point.getX() <= Math.max(pntStart.getX(), pntEnd.getX()) + tolerance
					&& point.getY() >= Math.min(pntStart.getY(), pntEnd.getY()) - tolerance
					&& point.getY() <= Math.max(pntStart.getY(), pntEnd.getY()) + tolerance) {
				bResult = true;
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return bResult;
	}
}
