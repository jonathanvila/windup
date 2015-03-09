package org.jboss.windup.config.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.util.Annotations;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.windup.config.RuleProvider;
import org.jboss.windup.config.phase.RulePhase;
import org.ocpsoft.rewrite.config.Rule;

/**
 * Fluent builder for creating {@link RuleProviderMetadata} instances. Provides sensible defaults using given required
 * values.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MetadataBuilder extends AbstractMetadata implements RuleProviderMetadata
{
    private Class<? extends RuleProvider> implementationType;
    private String origin;
    private Class<? extends RulePhase> phase;

    private List<Class<? extends RuleProvider>> executeAfter = new ArrayList<>();
    private List<String> executeAfterIDs = new ArrayList<>();
    private List<Class<? extends RuleProvider>> executeBefore = new ArrayList<>();
    private List<String> executeBeforeIDs = new ArrayList<>();
    private Set<String> tags = new HashSet<>();

    private MetadataBuilder(Class<? extends RuleProvider> implementationType, String providerId)
    {
        super(providerId);
        this.implementationType = implementationType;
    }

    /**
     * Create a new {@link RuleProviderMetadata} builder instance for the given {@link Addon} and {@link RuleProvider}
     * type, using the provided parameters to seed sensible defaults.
     */
    public static MetadataBuilder forProvider(Class<? extends RuleProvider> implementationType)
    {
        String id = implementationType.getSimpleName();

        Metadata metadata = Annotations.getAnnotation(implementationType, Metadata.class);
        if (metadata != null && !metadata.id().isEmpty())
            id = metadata.id();

        return forProvider(implementationType, id);
    }

    /**
     * Create a new {@link RuleProviderMetadata} builder instance for the given {@link Addon}, {@link RuleProvider}
     * type, and {@link String} ID, using the provided parameters to seed sensible defaults.
     */
    public static MetadataBuilder forProvider(Class<? extends RuleProvider> implementationType, String providerId)
    {
        Assert.notNull(implementationType, "Rule provider Implementation type must not be null.");
        Assert.notNull(providerId, "Rule provider ID must not be null.");

        MetadataBuilder builder = new MetadataBuilder(implementationType, providerId)
                    .setOrigin(implementationType.getName() + " loaded from " + implementationType.getClassLoader().toString());

        Metadata metadata = Annotations.getAnnotation(implementationType, Metadata.class);
        if (metadata != null)
        {
            Class<? extends RuleProvider>[] after = metadata.after();
            if (after.length > 0)
                builder.setExecuteAfter(Arrays.asList(after));

            String[] afterIDs = metadata.afterIDs();
            if (afterIDs.length > 0)
                builder.setExecuteAfterIDs(Arrays.asList(afterIDs));

            Class<? extends RuleProvider>[] before = metadata.before();
            if (before.length > 0)
                builder.setExecuteBefore(Arrays.asList(before));

            String[] beforeIDs = metadata.beforeIDs();
            if (beforeIDs.length > 0)
                builder.setExecuteBeforeIDs(Arrays.asList(beforeIDs));

            builder.setPhase(metadata.phase());

            String[] tags = metadata.tags();
            if (tags.length > 0)
                builder.setTags(Arrays.asList(tags));

        }

        return builder;
    }

    @Override
    public Class<? extends RuleProvider> getType()
    {
        return implementationType;
    }

    /*
     * Overrides from superclass.
     */
    @Override
    public String getOrigin()
    {
        return origin == null ? super.getOrigin() : origin;
    }

    /**
     * Set the descriptive information indicating where the corresponding {@link RuleProvider} instance is located (eg,
     * a path to an XML file on disk, or an {@link Addon} coordinate and class name).
     */
    public MetadataBuilder setOrigin(String origin)
    {
        this.origin = origin;
        return this;
    }

    @Override
    public Class<? extends RulePhase> getPhase()
    {
        return phase == null ? super.getPhase() : phase;
    }

    /**
     * Set the {@link RulePhase} in which the {@link Rule} instances from the corresponding {@link RuleProvider}
     * instance should be executed.
     * <p>
     * The default phase is {@link RulePhase#MIGRATION_RULES}.
     */
    public MetadataBuilder setPhase(Class<? extends RulePhase> phase)
    {
        this.phase = phase;
        return this;
    }

    @Override
    public List<Class<? extends RuleProvider>> getExecuteAfter()
    {
        return executeAfter == null ? super.getExecuteAfter() : executeAfter;
    }

    /**
     * Set the list of {@link RuleProvider} classes that should execute before the {@link Rule} instances in the
     * corresponding {@link RuleProvider} instance.
     *
     * <p>
     * {@link RuleProvider} references can also be specified based on id ({@link #getExecuteAfterID}).
     */
    public MetadataBuilder setExecuteAfter(List<Class<? extends RuleProvider>> executeAfter)
    {
        this.executeAfter = new ArrayList<>(executeAfter);
        return this;
    }

    /**
     * Ad an entry to the list of {@link RuleProvider} classes that should execute after the {@link Rule} instances in
     * the corresponding {@link RuleProvider} instance.
     *
     * {@link RuleProvider}s can also be specified based on id ({@link #getExecuteBeforeID}).
     */
    public MetadataBuilder addExecuteAfter(Class<? extends RuleProvider> type)
    {
        if (type != null)
        {
            executeAfter.add(type);
        }
        return this;
    }

    @Override
    public List<String> getExecuteAfterIDs()
    {
        return executeAfterIDs == null ? super.getExecuteAfterIDs() : executeAfterIDs;
    }

    /**
     * Set the list of the {@link RuleProvider} classes that should execute before the {@link Rule} instances in the
     * corresponding {@link RuleProvider} instance.
     *
     * <p>
     * This is returned as a list of Rule IDs in order to support extensions that cannot depend on each other via class
     * names. For example, in the case of the Groovy rules extension, a single class covers many rules with their own
     * IDs.
     *
     * For specifying Java-based rules, {@link #getExecuteAfter()} is preferred.
     */
    public MetadataBuilder setExecuteAfterIDs(List<String> executeAfterIDs)
    {
        this.executeAfterIDs = new ArrayList<>(executeAfterIDs);
        return this;
    }

    /**
     * Add an entry to the list of the {@link RuleProvider} classes that should execute before the {@link Rule}
     * instances in the corresponding {@link RuleProvider} instance.
     *
     * <p>
     * This is returned as a list of Rule IDs in order to support extensions that cannot depend on each other via class
     * names. For example, in the case of the Groovy rules extension, a single class covers many rules with their own
     * IDs.
     *
     * For specifying Java-based rules, {@link #getExecuteAfter()} is preferred.
     */
    public MetadataBuilder addExecuteAfterId(String id)
    {
        if (id != null)
        {
            executeAfterIDs.add(id);
        }
        return this;
    }

    @Override
    public List<Class<? extends RuleProvider>> getExecuteBefore()
    {
        return executeBefore == null ? super.getExecuteBefore() : executeBefore;
    }

    /**
     * Set the list of {@link RuleProvider} classes that should execute after the {@link Rule} instances in the
     * corresponding {@link RuleProvider} instance.
     *
     * {@link RuleProvider}s can also be specified based on id ({@link #getExecuteBeforeID}).
     */
    public MetadataBuilder setExecuteBefore(List<Class<? extends RuleProvider>> executeBefore)
    {
        this.executeBefore = new ArrayList<>(executeBefore);
        return this;
    }

    /**
     * Ad an entry to the list of {@link RuleProvider} classes that should execute after the {@link Rule} instances in
     * the corresponding {@link RuleProvider} instance.
     *
     * {@link RuleProvider}s can also be specified based on id ({@link #getExecuteBeforeID}).
     */
    public MetadataBuilder addExecuteBefore(Class<? extends RuleProvider> type)
    {
        if (type != null)
        {
            executeBefore.add(type);
        }
        return this;
    }

    @Override
    public List<String> getExecuteBeforeIDs()
    {
        return executeBeforeIDs == null ? super.getExecuteBeforeIDs() : executeBeforeIDs;
    }

    /**
     * Set the list of the {@link RuleProvider} classes that should execute after the {@link Rule} instances in the
     * corresponding {@link RuleProvider} instance.
     *
     * <p>
     * This is returned as a list of Rule IDs in order to support extensions that cannot depend on each other via class
     * names. For example, in the case of the Groovy rules extension, a single class covers many rules with their own
     * IDs.
     *
     * For specifying Java-based rules, {@link #getExecuteBefore()} is preferred.
     */
    public MetadataBuilder setExecuteBeforeIDs(List<String> executeBeforeIDs)
    {
        this.executeBeforeIDs = new ArrayList<>(executeBeforeIDs);
        return this;
    }

    /**
     * Add to the list of the {@link RuleProvider} classes that should execute after the {@link Rule} instances in the
     * corresponding {@link RuleProvider} instance.
     *
     * <p>
     * This is returned as a list of Rule IDs in order to support extensions that cannot depend on each other via class
     * names. For example, in the case of the Groovy rules extension, a single class covers many rules with their own
     * IDs.
     *
     * For specifying Java-based rules, {@link #getExecuteBefore()} is preferred.
     */
    public MetadataBuilder addExecuteBeforeId(String id)
    {
        if (id != null)
        {
            executeBeforeIDs.add(id);
        }
        return this;
    }

    public MetadataBuilder addTags(String tag, String... tags)
    {
        if (!StringUtils.isBlank(tag))
            this.tags.add(tag.trim());

        if (tags != null)
        {
            for (String t : tags)
            {
                if (!StringUtils.isBlank(t))
                    this.tags.add(t.trim());
            }
        }
        return this;
    }

    @Override
    public Set<String> getTags()
    {
        return this.tags.isEmpty() ? super.getTags() : this.tags;
    }

    public void setTags(List<String> tags)
    {
        if (tags == null)
            this.tags = new HashSet<>();
        else
            this.tags = Collections.unmodifiableSet(new HashSet<>(tags));
    }
}
