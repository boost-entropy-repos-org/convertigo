package com.twinsoft.convertigo.engine.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twinsoft.convertigo.beans.core.Project;
import com.twinsoft.convertigo.beans.core.UrlMapper;
import com.twinsoft.convertigo.beans.core.UrlMapping;
import com.twinsoft.convertigo.beans.core.UrlMappingOperation;
import com.twinsoft.convertigo.beans.core.UrlMappingParameter;
import com.twinsoft.convertigo.beans.core.UrlMappingParameter.Type;
import com.twinsoft.convertigo.engine.EnginePropertiesManager;
import com.twinsoft.convertigo.engine.EnginePropertiesManager.PropertyName;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;

public class SwaggerUtils {

	public static Swagger read(String url) {
		return new SwaggerParser().read(url);
	}
	
	public static Swagger parse(Collection<UrlMapper> collection) {
		Swagger swagger = new Swagger();
		
		Contact contact = new Contact();
		contact.setEmail("support@convertigo.com");
		
		Info info = new Info();
		info.setTitle("Convertigo REST API");
		info.setDescription("Find here all deployed projects");
		info.setVersion(com.twinsoft.convertigo.engine.Version.fullProductVersion);
		info.setContact(contact);
		swagger.setInfo(info);
		
		String webAppPath = EnginePropertiesManager.getProperty(PropertyName.APPLICATION_SERVER_CONVERTIGO_URL);
		int index = webAppPath.indexOf("://") + 3;
		String host = webAppPath.substring(index, webAppPath.indexOf('/', index));
		String basePath = webAppPath.substring(index + host.length()) + "/api";
		swagger.setHost(host);
		swagger.setBasePath(basePath);
		
		List<Scheme> schemes = new ArrayList<Scheme>();
		schemes.add(Scheme.HTTP);
		schemes.add(Scheme.HTTPS);
		swagger.setSchemes(schemes);
		
		swagger.setProduces(Arrays.asList("application/json", "application/xml"));
		
		List<Tag> tags = new ArrayList<Tag>();
		Map<String, Path> paths = new HashMap<String, Path>();
		for (UrlMapper urlMapper : collection) {
			if (urlMapper != null) {
				Swagger p_swagger = parse(urlMapper);
				if (p_swagger != null) {
					if (p_swagger != null) {
						Tag tag = new Tag();
						tag.setName(urlMapper.getProject().getName());
						tag.setDescription(urlMapper.getProject().getComment());
						tags.add(tag);
						
						paths.putAll(p_swagger.getPaths());
					}
				}
			}
		}
		swagger.setTags(tags);
		swagger.setPaths(paths);
		
		return swagger;
	}
	
	public static Swagger parse(UrlMapper urlMapper) {
		Swagger swagger = new Swagger();
		
		Contact contact = new Contact();
		contact.setEmail("support@convertigo.com");
		
		Project project = urlMapper.getProject();
		Info info = new Info();
		info.setTitle("Convertigo REST API for " + project.getName());
		info.setDescription(project.getComment());
		info.setVersion(project.getVersion());
		info.setContact(contact);
		swagger.setInfo(info);
		
		String webAppPath = EnginePropertiesManager.getProperty(PropertyName.APPLICATION_SERVER_CONVERTIGO_URL);
		int index = webAppPath.indexOf("://") + 3;
		String host = webAppPath.substring(index, webAppPath.indexOf('/', index));
		String basePath = webAppPath.substring(index + host.length()) + "/api";
		swagger.setHost(host);
		swagger.setBasePath(basePath);
		
		List<Scheme> schemes = new ArrayList<Scheme>();
		schemes.add(Scheme.HTTP);
		schemes.add(Scheme.HTTPS);
		swagger.setSchemes(schemes);
		
		swagger.setProduces(Arrays.asList("application/json", "application/xml"));
		
		Map<String, Path> swagger_paths = new HashMap<String, Path>();
		try {
			for (UrlMapping urlMapping: urlMapper.getMappingList()) {
				Path swagger_path = new Path();
				for (UrlMappingOperation umo : urlMapping.getOperationList()) {
					Operation s_operation = new Operation();
					s_operation.setOperationId(umo.getName());
					s_operation.setProduces(Arrays.asList("application/json", "application/xml"));
					
					// Set operation tags
					List<String> tags = Arrays.asList(""+ project.getName());
					s_operation.setTags(tags);
					
					// Set operation responses
					Map<String, Response> responses = new HashMap<String, Response>();
					Response resp200 = new Response();
					resp200.description("successful operation");
					responses.put("200", resp200);
					s_operation.setResponses(responses);
					
					// Set operation parameters
					List<Parameter> s_parameters = new ArrayList<Parameter>();
					for (String pathVarName: urlMapping.getPathVariables()) {
						PathParameter s_parameter = new PathParameter();
						s_parameter.setName(pathVarName);
						s_parameter.setRequired(true);
						s_parameters.add(s_parameter);
					}
					for (UrlMappingParameter ump: umo.getParameterList()) {
						Parameter s_parameter = null;
						if (ump.getType() == Type.Query) {
							s_parameter = new QueryParameter();
						}
						if (s_parameter != null) {
							s_parameter.setName(ump.getName());
							s_parameter.setDescription(ump.getComment());
							s_parameter.setRequired(ump.isRequired());
							s_parameters.add(s_parameter);
						}
					}
					s_operation.setParameters(s_parameters);
					
					// Add operation to path
					String s_method = umo.getMethod().toLowerCase();
					swagger_path.set(s_method, s_operation);
				}
				swagger_paths.put(urlMapping.getPath(), swagger_path);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		swagger.setPaths(swagger_paths);
		
		return swagger;
	}
	
	public static String getYamlDefinition(Object object) throws JsonProcessingException {
		if (object instanceof UrlMapper) {
			return prettyPrintYaml(parse((UrlMapper)object));
		}
		if (object instanceof Collection<?>) {
			Collection<UrlMapper> collection = GenericUtils.cast(object);
			return prettyPrintYaml(parse(collection));
		}
		return null;
	}

	public static String getJsonDefinition(Object object) {
		if (object instanceof UrlMapper) {
			return prettyPrintJson(parse((UrlMapper)object));
		}
		if (object instanceof Collection<?>) {
			Collection<UrlMapper> collection = GenericUtils.cast(object);
			return prettyPrintJson(parse(collection));
		}
		return null;
	}
	
	public static String prettyPrintJson(Swagger swagger) {
		return Json.pretty(swagger);
	}
	
	public static String prettyPrintYaml(Swagger swagger) throws JsonProcessingException {
		return Yaml.pretty().writeValueAsString(swagger);
	}

	public static void testReadJson() {
		Swagger swagger = read("http://petstore.swagger.io/v2/swagger.json");
		if (swagger != null) {
			Json.prettyPrint(swagger);
			Yaml.prettyPrint(swagger);
		}		
	}
	
	public static void testReadYaml() {
		Swagger swagger = read("http://petstore.swagger.io/v2/swagger.yaml");
		if (swagger != null) {
			Json.prettyPrint(swagger);
			Yaml.prettyPrint(swagger);
		}		
	}
	
	public static void testReadPath() throws JsonProcessingException, IOException {
		String data = "{"
				+ "\"post\": { \"tags\": [\"pet\"], \"summary\": \"add a new pet to the store\", \"description\": \"\", \"operationid\": \"addpet\", \"consumes\": [\"application/json\", \"application/xml\"], \"produces\": [\"application/xml\", \"application/json\"], \"parameters\": [{ \"in\": \"body\", \"name\": \"body\", \"description\": \"pet object that needs to be added to the store\", \"required\": true, \"schema\": { \"$ref\": \"#/definitions/pet\" } }], \"responses\": { \"405\": { \"description\": \"invalid input\" } }, \"security\": [{ \"petstore_auth\": [\"write:pets\", \"read:pets\"] }] },"
				+ "\"put\": { \"tags\": [\"pet\"], \"summary\": \"update an existing pet\", \"description\": \"\", \"operationid\": \"updatepet\", \"consumes\": [\"application/json\", \"application/xml\"], \"produces\": [\"application/xml\", \"application/json\"], \"parameters\": [{ \"in\": \"body\", \"name\": \"body\", \"description\": \"pet object that needs to be added to the store\", \"required\": true, \"schema\": { \"$ref\": \"#/definitions/pet\" } }], \"responses\": { \"400\": { \"description\": \"invalid id supplied\" }, \"404\": { \"description\": \"pet not found\" }, \"405\": { \"description\": \"validation exception\" } }, \"security\": [{ \"petstore_auth\": [\"write:pets\", \"read:pets\"] }] }"
				+ "}";
		ObjectMapper mapper = Json.mapper();
		JsonNode pathNode = mapper.readTree(data);
		Path path = mapper.convertValue(pathNode, Path.class);
		Json.prettyPrint(path);
	}

/*
	public static void main(String[] args) {
		try {
			testReadJson();
			testReadYaml();
			testReadPath();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
*/
}