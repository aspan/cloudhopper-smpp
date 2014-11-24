package com.cloudhopper.smpp.type;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2012 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.util.ByteBufUtil;
import com.cloudhopper.smpp.util.PduUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author innar.made@ishisystems.com
 */
public class SubmitMultiDestinationAddress {

    protected byte destinationFlag;
    protected Address address;
    protected String distributionListName;

    public SubmitMultiDestinationAddress() {
    }

    public SubmitMultiDestinationAddress(Address address) {
        this.destinationFlag = SmppConstants.SM_DEST_SME_ADDRESS;
        this.address = address;
    }

    public SubmitMultiDestinationAddress(String distributionListName) {
        this.destinationFlag = SmppConstants.SM_DEST_DL_NAME;
        this.distributionListName = distributionListName;
    }

    public byte getDestinationFlag() {
        return this.destinationFlag;
    }

    public void setDestinationFlag(byte destinationFlag) {
        this.destinationFlag = destinationFlag;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDistributionListName() {
        return this.distributionListName;
    }

    public void setDistributionListName(String distributionListName) {
        this.distributionListName = distributionListName;
    }

    public boolean isAddress() {
        return this.destinationFlag == SmppConstants.SM_DEST_SME_ADDRESS;
    }

    public boolean isDistributionListName() {
        return this.destinationFlag == SmppConstants.SM_DEST_DL_NAME;
    }

    public int calculateByteSize() {
        int size = 1;
        if (isAddress()) {
            size += PduUtil.calculateByteSizeOfAddress(this.address);
        } else if (isDistributionListName()) {
            size += PduUtil.calculateByteSizeOfNullTerminatedString(this.distributionListName);
        }
        return size;
    }

    public void read(ByteBuf buffer) throws UnrecoverablePduException, RecoverablePduException {
        destinationFlag = buffer.readByte();
        if (isAddress()) {
            address = ByteBufUtil.readAddress(buffer);
        } else if (isDistributionListName()) {
            distributionListName = ByteBufUtil.readNullTerminatedString(buffer);
        } else {
            throwInvalidDestinationFlagException();
        }
    }

    public void write(ByteBuf buffer) throws UnrecoverablePduException, RecoverablePduException {
        buffer.writeByte(destinationFlag);
        if (isAddress()) {
            ByteBufUtil.writeAddress(buffer, this.address);
        } else if (isDistributionListName()) {
            ByteBufUtil.writeNullTerminatedString(buffer, this.distributionListName);
        } else {
            throwInvalidDestinationFlagException();
        }
    }

    protected void throwInvalidDestinationFlagException() throws SmppInvalidArgumentException {
        String msg = "Invalid submit_multi dest_flag value %s, valid values are 1 = SME Address or 2 = Distribution List Name";
        throw new SmppInvalidArgumentException(String.format(msg, destinationFlag));
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("dest_flag=0x").append(HexUtil.toHexString(destinationFlag));
        buffer.append(" address=");
        buffer.append(StringUtil.toStringWithNullAsEmpty(address));
        buffer.append(" distribution_list_name=");
        buffer.append(StringUtil.toStringWithNullAsEmpty(distributionListName));
        return buffer.toString();
    }
}
