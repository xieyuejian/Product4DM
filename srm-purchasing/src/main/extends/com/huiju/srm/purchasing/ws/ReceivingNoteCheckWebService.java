/*
 * Copyright 2002-2014 the original author or authors.
 *
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
 */
package com.huiju.srm.purchasing.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.purchasing.dto.ReceivingNoteCheckDto;

/**
 * 收退货信息接口
 * 
 * @author zhuang.jq
 */
@WebService(name = "receivingNoteCheckWebService", targetNamespace = "http://www.huiju.com/receivingNoteCheck")
public interface ReceivingNoteCheckWebService {

    @WebResult(name = "result")
    @WebMethod(operationName = "receivingNoteCheck")
    Message receivingNoteCheck(@WebParam(name = "orders") List<ReceivingNoteCheckDto> orders);

}
