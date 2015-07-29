package com.eloraam.redpower.core;

import com.eloraam.redpower.core.WorldCoord;

public interface IFrameLink {

   boolean isFrameMoving();

   boolean canFrameConnectIn(int var1);

   boolean canFrameConnectOut(int var1);

   WorldCoord getFrameLinkset();
}
