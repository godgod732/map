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
import org.web3j.abi.datatypes.generated.Int256;
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
    private static final String BINARY = "608060405234801561001057600080fd5b50610eac806100206000396000f3006080604052600436106100985763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166307d5ebf5811461009d57806329632d00146101025780632a9dd71b146101295780637f33cb181461014657806397aae09e1461028a5780639fe875c7146102ad578063a9858db514610309578063add60d2c1461034b578063ce04d2ad14610415575b600080fd5b3480156100a957600080fd5b506100b2610439565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156100ee5781810151838201526020016100d6565b505050509050019250505060405180910390f35b34801561010e57600080fd5b5061011761049c565b60408051918252519081900360200190f35b34801561013557600080fd5b506101446004356024356104b2565b005b34801561015257600080fd5b50610167600160a060020a0360043516610522565b6040518080602001806020018060200180602001858103855289818151815260200191508051906020019060200280838360005b838110156101b357818101518382015260200161019b565b50505050905001858103845288818151815260200191508051906020019060200280838360005b838110156101f25781810151838201526020016101da565b50505050905001858103835287818151815260200191508051906020019060200280838360005b83811015610231578181015183820152602001610219565b50505050905001858103825286818151815260200191508051906020019060200280838360005b83811015610270578181015183820152602001610258565b505050509050019850505050505050505060405180910390f35b34801561029657600080fd5b50610144600435600390810b90602435900b6108fb565b3480156102b957600080fd5b506102e660048035600160a060020a0316906024803580820192908101359160443590810191013561091b565b60408051600160a060020a03909316835260208301919091528051918290030190f35b34801561031557600080fd5b5061032a600160a060020a0360043516610b0f565b60408051938452602084019290925260ff1682820152519081900360600190f35b34801561035757600080fd5b5061036f600160a060020a0360043516602435610b44565b6040518080602001806020018460ff1660ff168152602001838103835286818151815260200191508051906020019060200280838360005b838110156103bf5781810151838201526020016103a7565b50505050905001838103825285818151815260200191508051906020019060200280838360005b838110156103fe5781810151838201526020016103e6565b505050509050019550505050505060405180910390f35b34801561042157600080fd5b5061014463ffffffff6004351660ff60243516610d25565b6060600180548060200260200160405190810160405280929190818152602001828054801561049157602002820191906000526020600020905b8154600160a060020a03168152600190910190602001808311610473575b505050505090505b90565b3360009081526020819052604090206002015490565b6001805480820182557fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805473ffffffffffffffffffffffffffffffffffffffff1916339081179091556000908152602081905260409020928355820155600301805464ff0000000019169055565b600160a060020a03811660009081526020818152604080832060030154815163ffffffff9091168082528084028201909301909152606092839283928392918391829182918291869182918015610583578160200160208202803883390190505b50600160a060020a038d166000908152602081815260409182902060030154825163ffffffff90911680825280830282019092019092529197509080156105d4578160200160208202803883390190505b50600160a060020a038d166000908152602081815260409182902060030154825163ffffffff9091168082528083028201909201909252919650908015610625578160200160208202803883390190505b50600160a060020a038d166000908152602081815260409182902060030154825163ffffffff9091168082528083028201909201909252919550908015610676578160200160208202803883390190505b509250600091505b600160a060020a038c1660009081526020819052604090206002015463ffffffff831610156108ea575060005b600160a060020a038c166000908152602081905260409020600201805463ffffffff84169081106106d857fe5b600091825260209091206003909102015463ffffffff821610156108df57600160a060020a038c166000908152602081905260409020600201805463ffffffff841690811061072357fe5b90600052602060002090600302016000018163ffffffff1681548110151561074757fe5b9060005260206000200154868863ffffffff1681518110151561076657fe5b6020908102909101810191909152600160a060020a038d1660009081529081905260409020600201805463ffffffff84169081106107a057fe5b90600052602060002090600302016001018163ffffffff168154811015156107c457fe5b9060005260206000200154858863ffffffff168151811015156107e357fe5b6020908102909101810191909152600160a060020a038d1660009081529081905260409020600201805463ffffffff841690811061081d57fe5b60009182526020909120600260039092020101548351600160a060020a0390911690849063ffffffff8a1690811061085157fe5b600160a060020a039283166020918202909201810191909152908d1660009081529081905260409020600201805463ffffffff841690811061088f57fe5b906000526020600020906003020160020160149054906101000a900460ff16848863ffffffff168151811015156108c257fe5b60ff909216602092830290910190910152600196870196016106ab565b60019091019061067e565b50939a929950975095509350505050565b336000908152602081905260409020600392830b8155910b600190910155565b600160a060020a038516600090815260208181526040808320815192870280840160a090810190935260808401888152859460029093019383928b918b91829186019084908082843782019150505050505081526020018686808060200260200160405190810160405280939291908181526020018383602002808284375050509284525050336020808401919091526000604090930183905284546001810180875595845292819020845180516003909502909101936109e3935084929190910190610e1b565b5060208281015180516109fc9260018501920190610e1b565b506040828101516002928301805460609095015160ff16740100000000000000000000000000000000000000000274ff000000000000000000000000000000000000000019600160a060020a0393841673ffffffffffffffffffffffffffffffffffffffff19909716969096179590951694909417909355918a16600081815260208181529084902060038101805463ffffffff8082168e011663ffffffff19909116179055909201548351600019919091018152925190935033927fad60ddff867e8ab165bd55651c31e944744902b909680848794d3ffb1ae73fb692908290030190a35050600160a060020a0385166000908152602081905260409020600201548590600019019550959350505050565b600160a060020a0316600090815260208190526040902080546001820154600390920154909264010000000090910460ff1690565b600160a060020a038216600090815260208190526040812060020180546060928392909133919086908110610b7557fe5b6000918252602090912060026003909202010154600160a060020a03161480610ba65750600160a060020a03851633145b1515610bb157600080fd5b600160a060020a0385166000908152602081905260409020600201805485908110610bd857fe5b60009182526020808320600160a060020a038916845290839052604090922060020180546003909202909201919086908110610c1057fe5b906000526020600020906003020160010160008088600160a060020a0316600160a060020a0316815260200190815260200160002060020186815481101515610c5557fe5b906000526020600020906003020160020160149054906101000a900460ff1682805480602002602001604051908101604052809291908181526020018280548015610cbf57602002820191906000526020600020905b815481526020019060010190808311610cab575b5050505050925081805480602002602001604051908101604052809291908181526020018280548015610d1157602002820191906000526020600020905b815481526020019060010190808311610cfd575b505050505091509250925092509250925092565b336000908152602081905260409020600201805482919063ffffffff8516908110610d4c57fe5b6000918252602080832060026003909302018201805460ff95909516740100000000000000000000000000000000000000000274ff00000000000000000000000000000000000000001990951694909417909355338252918190526040902001805463ffffffff8416908110610dbe57fe5b6000918252602091829020600260039092020101546040805160ff851681529051600160a060020a039092169233927f5684f59433d5e303db49e88b902b89e0530fee26c2c35577602535ca3b73b17b9281900390910190a35050565b828054828255906000526020600020908101928215610e56579160200282015b82811115610e56578251825591602001919060010190610e3b565b50610e62929150610e66565b5090565b61049991905b80821115610e625760008155600101610e6c5600a165627a7a7230582054329fb4a246f80d5d87cc35295ec667eb13fbf975ce887e39a3959a24ea9aab0029";

    public static final String FUNC_GETDRONES = "getDrones";

    public static final String FUNC_RETURNMISSIONLENGTH = "returnMissionLength";

    public static final String FUNC_REGISTERDRONE = "registerDrone";

    public static final String FUNC_TRACEFLIGHTHISTORY = "traceFlightHistory";

    public static final String FUNC_UPDATEDRONEPOS = "updateDronePos";

    public static final String FUNC_SETWAYPOINT = "setWayPoint";

    public static final String FUNC_GETDRONESTATEBYADDR = "getDroneStateByAddr";

    public static final String FUNC_GETMISSION = "getMission";

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

    public RemoteCall<BigInteger> returnMissionLength() {
        final Function function = new Function(FUNC_RETURNMISSIONLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> registerDrone(BigInteger _latitude, BigInteger _longitude) {
        final Function function = new Function(
                FUNC_REGISTERDRONE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_latitude), 
                new org.web3j.abi.datatypes.generated.Int256(_longitude)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>>> traceFlightHistory(String _drone) {
        final Function function = new Function(FUNC_TRACEFLIGHTHISTORY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int256>>() {}, new TypeReference<DynamicArray<Int256>>() {}, new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Uint8>>() {}));
        return new RemoteCall<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>>>(
                new Callable<Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>>(
                                convertToNative((List<Int256>) results.get(0).getValue()), 
                                convertToNative((List<Int256>) results.get(1).getValue()), 
                                convertToNative((List<Address>) results.get(2).getValue()), 
                                convertToNative((List<Uint8>) results.get(3).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> updateDronePos(BigInteger _latitude, BigInteger _longitude) {
        final Function function = new Function(
                FUNC_UPDATEDRONEPOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int32(_latitude), 
                new org.web3j.abi.datatypes.generated.Int32(_longitude)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setWayPoint(String _drone, List<BigInteger> _latitude, List<BigInteger> _longitude) {
        final Function function = new Function(
                FUNC_SETWAYPOINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(_latitude, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(_longitude, org.web3j.abi.datatypes.generated.Int256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> getDroneStateByAddr(String _drone) {
        final Function function = new Function(FUNC_GETDRONESTATEBYADDR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_drone)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}, new TypeReference<Int256>() {}, new TypeReference<Uint8>() {}));
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
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int256>>() {}, new TypeReference<DynamicArray<Int256>>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>(
                new Callable<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>(
                                convertToNative((List<Int256>) results.get(0).getValue()), 
                                convertToNative((List<Int256>) results.get(1).getValue()), 
                                (BigInteger) results.get(2).getValue());
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
