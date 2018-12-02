package com.example.godgo.map;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Int32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class Dronechain extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610f94806100206000396000f3006080604052600436106100985763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166307d5ebf5811461009d57806326ebd5631461010257806329632d001461012757806374cbb2221461014e57806397aae09e146101aa578063a9858db5146101cd578063add60d2c14610219578063b846bb83146102e3578063ce04d2ad146103ef575b600080fd5b3480156100a957600080fd5b506100b2610413565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156100ee5781810151838201526020016100d6565b505050509050019250505060405180910390f35b34801561010e57600080fd5b50610125600435600390810b90602435900b610476565b005b34801561013357600080fd5b5061013c610517565b60408051918252519081900360200190f35b34801561015a57600080fd5b5061018760048035600160a060020a0316906024803580820192908101359160443590810191013561052d565b60408051600160a060020a03909316835260208301919091528051918290030190f35b3480156101b657600080fd5b50610125600435600390810b90602435900b610722565b3480156101d957600080fd5b506101ee600160a060020a0360043516610769565b60408051600394850b850b815292840b90930b602083015260ff168183015290519081900360600190f35b34801561022557600080fd5b5061023d600160a060020a03600435166024356107a5565b6040518080602001806020018460ff1660ff168152602001838103835286818151815260200191508051906020019060200280838360005b8381101561028d578181015183820152602001610275565b50505050905001838103825285818151815260200191508051906020019060200280838360005b838110156102cc5781810151838201526020016102b4565b505050509050019550505050505060405180910390f35b3480156102ef57600080fd5b5061030a600160a060020a036004351660ff602435166109d2565b60405180806020018060200180602001858152602001848103845288818151815260200191508051906020019060200280838360005b83811015610358578181015183820152602001610340565b50505050905001848103835287818151815260200191508051906020019060200280838360005b8381101561039757818101518382015260200161037f565b50505050905001848103825286818151815260200191508051906020019060200280838360005b838110156103d65781810151838201526020016103be565b5050505090500197505050505050505060405180910390f35b3480156103fb57600080fd5b5061012563ffffffff6004351660ff60243516610d9b565b6060600180548060200260200160405190810160405280929190818152602001828054801561046b57602002820191906000526020600020905b8154600160a060020a0316815260019091019060200180831161044d575b505050505090505b90565b600180548082019091557fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805473ffffffffffffffffffffffffffffffffffffffff19163390811790915560009081526020819052604090208054600392830b63ffffffff9081166401000000000267ffffffff00000000199590940b1663ffffffff199091161792909216178155600201805464ff0000000019169055565b3360009081526020819052604090206001015490565b600160a060020a038516600090815260208181526040808320815192870280840160a090810190935260808401888152859460019093019383928b918b91829186019084908082843782019150505050505081526020018686808060200260200160405190810160405280939291908181526020018383602002808284375050509284525050336020808401919091526000604090930183905284546001810180875595845292819020845180516003909502909101936105f5935084929190910190610e95565b50602082810151805161060e9260018501920190610e95565b506040828101516002928301805460609095015160ff16740100000000000000000000000000000000000000000274ff000000000000000000000000000000000000000019600160a060020a0393841673ffffffffffffffffffffffffffffffffffffffff19909716969096179590951694909417909355918a166000818152602081815290849020928301805463ffffffff8082168d011663ffffffff199091161790556001929092015483516000199091018152925190935033927fad60ddff867e8ab165bd55651c31e944744902b909680848794d3ffb1ae73fb692908290030190a35050600160a060020a0385166000908152602081905260409020600101548590600019019550959350505050565b3360009081526020819052604090208054600392830b63ffffffff9081166401000000000267ffffffff00000000199590940b1663ffffffff199091161792909216179055565b600160a060020a031660009081526020819052604090208054600290910154600382810b936401000000009384900490910b9290910460ff1690565b600160a060020a0382166000908152602081905260408120600101805460609283929091339190869081106107d657fe5b6000918252602090912060026003909202010154600160a060020a031614806108075750600160a060020a03851633145b151561081257600080fd5b600160a060020a038516600090815260208190526040902060010180548590811061083957fe5b60009182526020808320600160a060020a03891684529083905260409092206001018054600390920290920191908690811061087157fe5b906000526020600020906003020160010160008088600160a060020a0316600160a060020a03168152602001908152602001600020600101868154811015156108b657fe5b906000526020600020906003020160020160149054906101000a900460ff168280548060200260200160405190810160405280929190818152602001828054801561094657602002820191906000526020600020906000905b82829054906101000a900460030b60030b8152602001906004019060208260030104928301926001038202915080841161090f5790505b50505050509250818054806020026020016040519081016040528092919081815260200182805480156109be57602002820191906000526020600020906000905b82829054906101000a900460030b60030b815260200190600401906020826003010492830192600103820291508084116109875790505b505050505091509250925092509250925092565b600160a060020a03821660009081526020818152604080832060020154815163ffffffff909116808252808402820190930190915260609283928392829184918291829185918291908015610a31578160200160208202803883390190505b50600160a060020a038d166000908152602081815260409182902060020154825163ffffffff9091168082528083028201909201909252919650908015610a82578160200160208202803883390190505b50600160a060020a038d166000908152602081815260409182902060020154825163ffffffff9091168082528083028201909201909252919550908015610ad3578160200160208202803883390190505b509250600091505b600160a060020a038c1660009081526020819052604090206001015463ffffffff83161015610d81575060005b600160a060020a038c166000908152602081905260409020600101805463ffffffff8416908110610b3557fe5b600091825260209091206003909102015463ffffffff82161015610d7657600160a060020a038c166000908152602081905260409020600101805460ff8d16919063ffffffff8516908110610b8657fe5b600091825260209091206003909102016002015474010000000000000000000000000000000000000000900460ff161415610d6e57600160a060020a038c166000908152602081905260409020600101805463ffffffff8416908110610be857fe5b90600052602060002090600302016000018163ffffffff16815481101515610c0c57fe5b90600052602060002090600891828204019190066004029054906101000a900460030b858763ffffffff16815181101515610c4357fe5b600392830b90920b6020928302909101820152600160a060020a038d1660009081529081905260409020600101805463ffffffff8416908110610c8257fe5b90600052602060002090600302016001018163ffffffff16815481101515610ca657fe5b90600052602060002090600891828204019190066004029054906101000a900460030b848763ffffffff16815181101515610cdd57fe5b600392830b90920b6020928302909101820152600160a060020a038d1660009081529081905260409020600101805463ffffffff8416908110610d1c57fe5b60009182526020909120600260039092020101548351600160a060020a0390911690849063ffffffff8916908110610d5057fe5b600160a060020a039092166020928302909101909101526001909501945b600101610b08565b600190910190610adb565b50929a919950975063ffffffff9092169550909350505050565b336000908152602081905260409020600101805482919063ffffffff8516908110610dc257fe5b60009182526020808320600260039093020191909101805460ff94909416740100000000000000000000000000000000000000000274ff000000000000000000000000000000000000000019909416939093179092553381529081905260409020600101805463ffffffff8416908110610e3857fe5b6000918252602091829020600260039092020101546040805160ff851681529051600160a060020a039092169233927f5684f59433d5e303db49e88b902b89e0530fee26c2c35577602535ca3b73b17b9281900390910190a35050565b82805482825590600052602060002090600701600890048101928215610f375791602002820160005b83821115610f0557835183826101000a81548163ffffffff021916908360030b63ffffffff1602179055509260200192600401602081600301049283019260010302610ebe565b8015610f355782816101000a81549063ffffffff0219169055600401602081600301049283019260010302610f05565b505b50610f43929150610f47565b5090565b61047391905b80821115610f4357805463ffffffff19168155600101610f4d5600a165627a7a72305820da42e8980e5677fa327e65b20467f401885c2363ef56e55c4f1f5293fa0c8d080029";

    public static final String FUNC_GETDRONES = "getDrones";

    public static final String FUNC_REGISTERDRONE = "registerDrone";

    public static final String FUNC_RETURNMISSIONLENGTH = "returnMissionLength";

    public static final String FUNC_SETWAYPOINT = "setWayPoint";

    public static final String FUNC_UPDATEDRONEPOS = "updateDronePos";

    public static final String FUNC_GETDRONESTATEBYADDR = "getDroneStateByAddr";

    public static final String FUNC_GETMISSION = "getMission";

    public static final String FUNC_TRACEFLIGHTHISTORY = "traceFlightHistory";

    public static final String FUNC_UPDATESTATE = "updateState";

    public static final Event CREATEMISSION_EVENT = new Event("createMission",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event UPDATEMISSIONSTATE_EVENT = new Event("updateMissionState", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Dronechain(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }



    @Deprecated
    protected Dronechain(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    public RemoteCall<List> getDrones() {
        final Function function = new Function(FUNC_GETDRONES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<TransactionReceipt> registerDrone(BigInteger _latitude, BigInteger _longitude) {
        final Function function = new Function(
                FUNC_REGISTERDRONE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int32(_latitude), 
                new org.web3j.abi.datatypes.generated.Int32(_longitude)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> returnMissionLength() {
        final Function function = new Function(FUNC_RETURNMISSIONLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> setWayPoint(String _drone, List<BigInteger> _latitude, List<BigInteger> _longitude) {
        final Function function = new Function(
                FUNC_SETWAYPOINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int32>(
                        org.web3j.abi.Utils.typeMap(_latitude, org.web3j.abi.datatypes.generated.Int32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int32>(
                        org.web3j.abi.Utils.typeMap(_longitude, org.web3j.abi.datatypes.generated.Int32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateDronePos(BigInteger _latitude, BigInteger _longitude) {
        final Function function = new Function(
                FUNC_UPDATEDRONEPOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int32(_latitude), 
                new org.web3j.abi.datatypes.generated.Int32(_longitude)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> getDroneStateByAddr(String _drone) {
        final Function function = new Function(FUNC_GETDRONESTATEBYADDR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}, new TypeReference<Int32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>> getMission(String _drone, BigInteger _index) {
        final Function function = new Function(FUNC_GETMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone), 
                new org.web3j.abi.datatypes.generated.Uint256(_index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int32>>() {}, new TypeReference<DynamicArray<Int32>>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>(
                new Callable<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>(
                                convertToNative((List<Int32>) results.get(0).getValue()), 
                                convertToNative((List<Int32>) results.get(1).getValue()), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger>> traceFlightHistory(String _drone, BigInteger _state) {
        final Function function = new Function(FUNC_TRACEFLIGHTHISTORY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone), 
                new org.web3j.abi.datatypes.generated.Uint8(_state)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int32>>() {}, new TypeReference<DynamicArray<Int32>>() {}, new TypeReference<DynamicArray<Address>>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger>>(
                new Callable<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger>>() {
                    @Override
                    public Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger>(
                                convertToNative((List<Int32>) results.get(0).getValue()), 
                                convertToNative((List<Int32>) results.get(1).getValue()), 
                                convertToNative((List<Address>) results.get(2).getValue()), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> updateState(BigInteger _index, BigInteger _state) {
        final Function function = new Function(
                FUNC_UPDATESTATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(_index), 
                new org.web3j.abi.datatypes.generated.Uint8(_state)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<CreateMissionEventResponse> getCreateMissionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CREATEMISSION_EVENT, transactionReceipt);
        ArrayList<CreateMissionEventResponse> responses = new ArrayList<CreateMissionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CreateMissionEventResponse typedResponse = new CreateMissionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CreateMissionEventResponse> createMissionEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, CreateMissionEventResponse>() {
            @Override
            public CreateMissionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CREATEMISSION_EVENT, log);
                CreateMissionEventResponse typedResponse = new CreateMissionEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CreateMissionEventResponse> createMissionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CREATEMISSION_EVENT));
        return createMissionEventObservable(filter);
    }

    public List<UpdateMissionStateEventResponse> getUpdateMissionStateEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPDATEMISSIONSTATE_EVENT, transactionReceipt);
        ArrayList<UpdateMissionStateEventResponse> responses = new ArrayList<UpdateMissionStateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpdateMissionStateEventResponse typedResponse = new UpdateMissionStateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UpdateMissionStateEventResponse> updateMissionStateEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UpdateMissionStateEventResponse>() {
            @Override
            public UpdateMissionStateEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UPDATEMISSIONSTATE_EVENT, log);
                UpdateMissionStateEventResponse typedResponse = new UpdateMissionStateEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<UpdateMissionStateEventResponse> updateMissionStateEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPDATEMISSIONSTATE_EVENT));
        return updateMissionStateEventObservable(filter);
    }

    @Deprecated
    public static RemoteCall<Dronechain> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Dronechain.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }


    @Deprecated
    public static RemoteCall<Dronechain> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Dronechain.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static Dronechain load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Dronechain(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Dronechain load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Dronechain(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    public static class CreateMissionEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _index;
    }

    public static class UpdateMissionStateEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _index;
    }
}
